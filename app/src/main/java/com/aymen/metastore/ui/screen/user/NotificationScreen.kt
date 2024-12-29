package com.aymen.store.ui.screen.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
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
import com.aymen.metastore.model.entity.model.CommandLine
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
//        PDFGenerateScreen(context, cv,)
    }
}
@Composable
fun PDFGenerateScreen(context: Context, cv : CompanyViewModel, commandsLine : List<CommandLine>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(text = "Generate PDF Example", fontSize = 24.sp)
        Button(onClick = { generatePDF(context, commandsLine) }) {
            Text("Generate PDF")
        }
    }
}
fun generatePDF(context: Context, commandsLine: List<CommandLine>) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points (595x842)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: android.graphics.Canvas? = page.canvas
        val paint = Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 20f
        canvas?.drawText(commandsLine[0].invoice?.provider?.name?:"", 20f, 50f, paint)
        canvas?.drawText("CLIENT :", 20f, 160f, paint)
        canvas?.drawText("name : ${commandsLine[0].invoice?.client?.name?:""}", 20f, 180f, paint)
        paint.textSize = 16f
        canvas?.drawText(commandsLine[0].invoice?.provider?.phone?:"", 20f, 70f, paint)
        canvas?.drawText(commandsLine[0].invoice?.provider?.email?:"", 20f, 90f, paint)
        canvas?.drawText(commandsLine[0].invoice?.provider?.matfisc?:"", 400f, 50f, paint)
        canvas?.drawText(commandsLine[0].invoice?.provider?.address?:"", 400f, 70f, paint)


        canvas?.drawText(commandsLine[0].invoice?.code?.toString()?:"", 200f, 120f, paint)
        canvas?.drawText("invoice date : ${commandsLine[0].invoice?.lastModifiedDate?:""}", 200f, 150f, paint)

        canvas?.drawText("phone : ${commandsLine[0].invoice?.client?.phone?:""}", 20f, 210f, paint)
        canvas?.drawText("address : ${commandsLine[0].invoice?.client?.address?:""}", 400f, 180f, paint)
        canvas?.drawText("matricule fiscal : ${commandsLine[0].invoice?.client?.matfisc?:""}", 400f, 210f, paint)


        // Table Configuration
        val startX = 20f
        var startY = 300f
        val cellWidth = 60f
        val cellHeight = 40f // Doubled the header cell height

        val headers = listOf("Label", "Code", "Qte", "U", "TVA", "Prix Unit", "Tot Tva", "Tot Article", "Discount")
        val numCols = headers.size // Number of columns
        val numRows = commandsLine.size + 1 // Add 1 for header row

        paint.textSize = 14f

        // Function to split text into multiple lines based on width
        fun splitText(text: String, paint: Paint, maxWidth: Float): List<String> {
            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    lines.add(currentLine)
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
            return lines
        }

        // Make header bold
        paint.typeface = Typeface.DEFAULT_BOLD // Set bold typeface for header

        // Draw Top Border for Header Row with thicker line
        paint.strokeWidth = 2f // Thicker line for the header border
        canvas?.drawLine(startX, startY, startX + numCols * cellWidth, startY, paint)

        // Draw headers
        for (col in headers.indices) {
            val lines = splitText(headers[col], paint, cellWidth - 10)
            for ((lineIndex, line) in lines.withIndex()) {
                val textX = startX + col * cellWidth + 5 // Padding
                val textY = startY + lineIndex * 16 + 15 // Adjusted Line height
                canvas?.drawText(line, textX, textY, paint)
            }
        }

        // Draw Bottom Line of Header Row with thicker line
        canvas?.drawLine(startX, startY + cellHeight, startX + numCols * cellWidth, startY + cellHeight, paint)

        startY += cellHeight // Move to the first data row

        // Draw data rows
        for (row in 0 until commandsLine.size) {
            val command = commandsLine[row]

            // Track maximum row height for multiline content
            var maxRowHeight = cellHeight

            for (col in headers.indices) {
                // Map cell text based on column index
                val cellText = when (col) {
                    0 -> command.article?.article?.libelle ?: ""
                    1 -> command.article?.article?.code ?: ""
                    2 -> command.quantity.toString()
                    3 -> command.article?.unit.toString()
                    4 -> command.article?.article?.tva.toString()
                    5 -> command.article?.sellingPrice.toString()
                    6 -> command.totTva.toString()
                    7 -> command.prixArticleTot.toString()
                    8 -> command.discount.toString()
                    else -> ""
                }

                // Split text into lines and render each line
                val lines = splitText(cellText, paint, cellWidth - 10)
                for ((lineIndex, line) in lines.withIndex()) {
                    val textX = startX + col * cellWidth + 5 // Padding
                    val textY = startY + lineIndex * 16 + 20 // Line height
                    canvas?.drawText(line, textX, textY, paint)
                }

                // Update max row height if needed
                maxRowHeight = maxOf(maxRowHeight, 16f * lines.size + 10) // Account for spacing
            }

            // Draw horizontal line after each row
            canvas?.drawLine(startX, startY + maxRowHeight, startX + numCols * cellWidth, startY + maxRowHeight, paint)

            // Move Y position for the next row
            startY += maxRowHeight
        }

        // Draw vertical grid lines
        for (col in 0..numCols) {
            val x = startX + col * cellWidth
            canvas?.drawLine(x, 300f, x, startY, paint)
        }

        // Finalize PDF
        pdfDocument.finishPage(page)

        // Save PDF
        val pdfFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyInvoices")
        if (!pdfFolder.exists()) pdfFolder.mkdirs()
        val outputFile = File(pdfFolder, "${commandsLine[0].invoice?.code}.pdf")
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



//        cv.getBitmapFromURL("${BASE_URL}werehouse/image/spiga1.jpg/article/0") {
//
//            if (it != null) {
//                val imageX = 50f // X-coordinate for the image
//                val imageY = 500f // Y-coordinate for the image
//                val imageWidth = 200f // Desired image width
//                val imageHeight = 200f // Desired image height
//                val scaledBitmap = Bitmap.createScaledBitmap(
//                    it,
//                    imageWidth.toInt(),
//                    imageHeight.toInt(),
//                    true
//                )
//                canvas?.drawBitmap(scaledBitmap, imageX, imageY, null)
//            } else {
//                Log.e("PDF", "Failed to load image from URL")
//            }
//        }