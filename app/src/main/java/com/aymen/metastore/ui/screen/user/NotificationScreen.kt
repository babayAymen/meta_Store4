package com.aymen.store.ui.screen.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun NotificationScreen(){
    val shop : ShoppingViewModel = hiltViewModel()
    val cv : CompanyViewModel = hiltViewModel()
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        PDFGenerateScreen(context, cv)
    }
}
@Composable
fun PDFGenerateScreen(context: Context, cv : CompanyViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(text = "Generate PDF Example", fontSize = 24.sp)
        Button(onClick = { generatePDF(context, cv) }) {
            Text("Generate PDF")
        }
    }
}
fun generatePDF(context: Context, cv : CompanyViewModel) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points (595x842)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: android.graphics.Canvas? = page.canvas
        val paint = android.graphics.Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 20f
        canvas?.drawText("Hello, this is a PDF generated using PdfDocument.", 100f, 100f, paint)
        paint.textSize = 16f
        canvas?.drawText("This is another line of text.", 100f, 150f, paint)

        val startX = 50f
        val startY = 200f
        val cellWidth = 150f
        val cellHeight = 40f
        val numRows = 6
        val numCols = 3

        for (i in 0..numRows) {
            val y = startY + i * cellHeight
            canvas?.drawLine(startX, y, startX + numCols * cellWidth, y, paint)
        }
        for (i in 0..numCols) {
            val x = startX + i * cellWidth
            canvas?.drawLine(x, startY, x, startY + numRows * cellHeight, paint)
        }

        paint.textSize = 14f
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val cellText = "R${row + 1}C${col + 1}"
                val textX = startX + col * cellWidth + 10 // Offset to avoid edge
                val textY = startY + row * cellHeight + cellHeight / 2 + 5 // Vertically centered
                canvas?.drawText(cellText, textX, textY, paint)
            }
        }
        cv.getBitmapFromURL("${BASE_URL}werehouse/image/spiga1.jpg/article/1") {

            if (it != null) {
                val imageX = 50f // X-coordinate for the image
                val imageY = 500f // Y-coordinate for the image
                val imageWidth = 200f // Desired image width
                val imageHeight = 200f // Desired image height
                val scaledBitmap = Bitmap.createScaledBitmap(
                    it,
                    imageWidth.toInt(),
                    imageHeight.toInt(),
                    true
                )
                canvas?.drawBitmap(scaledBitmap, imageX, imageY, null)
            } else {
                Log.e("PDF", "Failed to load image from URL")
            }
        }



        pdfDocument.finishPage(page)
        val pdfFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyPDFs")
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs()
        }
        val outputFile = File(pdfFolder, "example_document.pdf")
        val fileOutputStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(fileOutputStream)
        pdfDocument.close()
        Log.d("PDF", "PDF generated at: ${outputFile.absolutePath}")
        Toast.makeText(context, "PDF Generated", Toast.LENGTH_SHORT).show()

    } catch (e: IOException) {
        Log.e("PDF", "Error generating PDF: ${e.message}")
        Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show()
    }
}


