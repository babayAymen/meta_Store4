package com.aymen.store.ui.screen.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.ui.screen.admin.stringToLocalDateTime
import com.aymen.metastore.util.ADDRESS
import com.aymen.metastore.util.CLIENT
import com.aymen.metastore.util.CODE
import com.aymen.metastore.util.CODE_INVOICE
import com.aymen.metastore.util.COMPANY_NAME
import com.aymen.metastore.util.DATE
import com.aymen.metastore.util.DISCOUNT
import com.aymen.metastore.util.EMAIL
import com.aymen.metastore.util.ERROR_PDF
import com.aymen.metastore.util.FILE_NAME
import com.aymen.metastore.util.HT
import com.aymen.metastore.util.LABEL
import com.aymen.metastore.util.MAT_FISC
import com.aymen.metastore.util.NAME
import com.aymen.metastore.util.PDF_GENERATED
import com.aymen.metastore.util.PHONE
import com.aymen.metastore.util.PRICE_UNIT
import com.aymen.metastore.util.QUANTITY
import com.aymen.metastore.util.TOTTVA
import com.aymen.metastore.util.TOT_ARTICLE
import com.aymen.metastore.util.TTC
import com.aymen.metastore.util.TVA
import com.aymen.metastore.util.UNIT
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
//        Button(onClick = { generatePDF(context, commandsLine) }) {
//            Text("Generate PDF")
//        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun generatePDF(context: Context, commandsLine: List<CommandLine>) {
    val isDiscounted = commandsLine.any { line -> line.discount != 0.0 }
    val date = stringToLocalDateTime(commandsLine[0].invoice?.lastModifiedDate)
    try {
        var startY = 50f
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points (595x842)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: android.graphics.Canvas? = page.canvas
        val paint = Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 20f
        canvas?.drawText(" $COMPANY_NAME ${commandsLine[0].invoice?.provider?.name}", 20f, startY, paint)
        canvas?.drawText(" $MAT_FISC ${commandsLine[0].invoice?.provider?.matfisc?:"..."}", 400f, startY, paint)
        paint.textSize = 16f
        startY += 20f
        canvas?.drawText("$PHONE ${commandsLine[0].invoice?.provider?.phone?:"..."}", 20f, startY, paint)
        canvas?.drawText("$ADDRESS ${commandsLine[0].invoice?.provider?.address?:"..."}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$EMAIL ${commandsLine[0].invoice?.provider?.email?:"..."}", 20f, startY, paint)

        startY += 40f
        canvas?.drawText("$CODE_INVOICE ${commandsLine[0].invoice?.code?.toString()}", 200f, startY, paint)
        startY += 20f
        canvas?.drawText("$DATE $date", 200f, startY, paint)
        startY += 20f
        canvas?.drawText(CLIENT, 20f, startY, paint)
        startY += 20f
        canvas?.drawText("$NAME ${commandsLine[0].invoice?.client?.name?: commandsLine[0].invoice?.person?.username}", 20f, startY, paint)
        canvas?.drawText("$ADDRESS ${commandsLine[0].invoice?.client?.address?: commandsLine[0].invoice?.person?.address?:"..."}", 400f, startY, paint)

        startY += 20f
        canvas?.drawText("$PHONE ${commandsLine[0].invoice?.client?.phone?: commandsLine[0].invoice?.person?.phone?:"..."}", 20f, startY, paint)
        canvas?.drawText("$MAT_FISC ${commandsLine[0].invoice?.client?.matfisc?:"..."}", 400f, startY, paint)

        val startX = 20f
        startY += 90f
        val cellWidth = 60f
        val cellHeight = 40f

        val headers = listOf(LABEL, CODE, QUANTITY, UNIT, TVA, PRICE_UNIT, TOTTVA, TOT_ARTICLE)+ if(isDiscounted) listOf( DISCOUNT)else{
            emptyList()
        }
        val numCols = headers.size

        paint.textSize = 14f

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
        paint.typeface = Typeface.DEFAULT_BOLD


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
                    8 -> if(isDiscounted) command.discount.toString() else null
                    else -> ""
                }

                // Split text into lines and render each line
                val lines = cellText?.let { splitText(it, paint, cellWidth - 10) }
                for ((lineIndex, line) in lines?.withIndex()!!) {
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
        paint.textSize = 16f
            if(commandsLine[0].invoice?.discount != null && commandsLine[0].invoice?.discount != 0.0) {
                startY += 20f
                canvas?.drawText(
                    "$DISCOUNT ${commandsLine[0].invoice?.discount}",
                    400f,
                    startY,
                    paint
                )
            }
        startY += 20f
        canvas?.drawText("$TOTTVA ${commandsLine[0].invoice?.tot_tva_invoice}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$HT ${commandsLine[0].invoice?.prix_article_tot}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$TTC ${commandsLine[0].invoice?.prix_invoice_tot}", 400f, startY, paint)
        // Finalize PDF
        pdfDocument.finishPage(page)

        // Save PDF
        val pdfFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), FILE_NAME)
        if (!pdfFolder.exists()) pdfFolder.mkdirs()
        val outputFile = File(pdfFolder, "${commandsLine[0].invoice?.code}.pdf")
        val fileOutputStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(fileOutputStream)
        pdfDocument.close()

        Log.d("PDF", "PDF generated at: ${outputFile.absolutePath}")
        Toast.makeText(context, PDF_GENERATED, Toast.LENGTH_SHORT).show()

    } catch (e: IOException) {
        Log.e("PDF", "Error generating PDF: ${e.message}")
        Toast.makeText(context, ERROR_PDF, Toast.LENGTH_SHORT).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateOrderPDF(context: Context, commandsLine: List<PurchaseOrderLine>) {
    val date = stringToLocalDateTime(commandsLine[0].invoice?.lastModifiedDate)
    try {
        var startY = 50f
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points (595x842)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: android.graphics.Canvas? = page.canvas
        val paint = Paint()

        paint.color = android.graphics.Color.BLACK
        paint.textSize = 20f

        // Company and client info
        canvas?.drawText("$COMPANY_NAME ${commandsLine[0].invoice?.provider?.name}", 20f, startY, paint)
        canvas?.drawText("$MAT_FISC ${commandsLine[0].invoice?.provider?.matfisc ?: "..."}", 400f, startY, paint)
        paint.textSize = 16f
        startY += 20f
        canvas?.drawText("$PHONE ${commandsLine[0].invoice?.provider?.phone ?: "..."}", 20f, startY, paint)
        canvas?.drawText("$ADDRESS ${commandsLine[0].invoice?.provider?.address ?: "..."}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$EMAIL ${commandsLine[0].invoice?.provider?.email ?: "..."}", 20f, startY, paint)

        startY += 40f
        canvas?.drawText("$CODE_INVOICE ${commandsLine[0].invoice?.code ?: "Unknown"}", 200f, startY, paint)
        startY += 20f
        canvas?.drawText("$DATE $date", 200f, startY, paint)
        startY += 20f
        canvas?.drawText(CLIENT, 20f, startY, paint)
        startY += 20f
        canvas?.drawText(
            "$NAME ${commandsLine[0].invoice?.client?.name ?: commandsLine[0].invoice?.person?.username}",
            20f,
            startY,
            paint
        )
        canvas?.drawText(
            "$ADDRESS ${commandsLine[0].invoice?.client?.address ?: commandsLine[0].invoice?.person?.address ?: "..."}",
            400f,
            startY,
            paint
        )
        startY += 20f
        canvas?.drawText(
            "$PHONE ${commandsLine[0].invoice?.client?.phone ?: commandsLine[0].invoice?.person?.phone ?: "..."}",
            20f,
            startY,
            paint
        )
        canvas?.drawText("$MAT_FISC ${commandsLine[0].invoice?.client?.matfisc ?: "..."}", 400f, startY, paint)

        // Table Configuration
        val startX = 20f
        startY += 90f
        val cellWidth = 60f
        val cellHeight = 40f
        val headers = listOf(LABEL, CODE, QUANTITY, UNIT, TVA, PRICE_UNIT, TOTTVA, TOT_ARTICLE)
        val numCols = headers.size

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

        // Draw Header
        paint.style = Paint.Style.FILL
        paint.color = android.graphics.Color.LTGRAY // Header background color
        canvas?.drawRect(startX, startY, startX + numCols * cellWidth, startY + cellHeight, paint)

        paint.color = android.graphics.Color.BLACK
        paint.typeface = Typeface.DEFAULT_BOLD

        for (col in headers.indices) {
            val lines = splitText(headers[col], paint, cellWidth - 10)
            for ((lineIndex, line) in lines.withIndex()) {
                val textX = startX + col * cellWidth + 5
                val textY = startY + lineIndex * 16 + 15
                canvas?.drawText(line, textX, textY, paint)
            }
        }

        startY += cellHeight

        // Draw Data Rows
        paint.typeface = Typeface.DEFAULT
        for (row in commandsLine.indices) {
            val command = commandsLine[row]

            // Alternating row colors
            val isEvenRow = row % 2 == 0
            paint.color = if (isEvenRow) android.graphics.Color.WHITE else android.graphics.Color.LTGRAY
            canvas?.drawRect(startX, startY, startX + numCols * cellWidth, startY + cellHeight, paint)

            paint.color = android.graphics.Color.BLACK
            for (col in headers.indices) {
                val cellText = when (col) {
                    0 -> command.article?.article?.libelle ?: ""
                    1 -> command.article?.article?.code ?: ""
                    2 -> command.quantity.toString()
                    3 -> command.article?.unit.toString()
                    4 -> command.article?.article?.tva.toString()
                    5 -> command.article?.sellingPrice.toString()
                    6 -> command.totTva.toString()
                    7 -> command.prixArticleTot.toString()
                    else -> ""
                }
                val lines = splitText(cellText, paint, cellWidth - 10)
                for ((lineIndex, line) in lines.withIndex()) {
                    val textX = startX + col * cellWidth + 5
                    val textY = startY + lineIndex * 16 + 15
                    canvas?.drawText(line, textX, textY, paint)
                }
            }
            startY += cellHeight
        }

        // Totals
        startY += 20f
        paint.textSize = 16f
        canvas?.drawText("$DISCOUNT ${commandsLine[0].invoice?.discount}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$TOTTVA ${commandsLine[0].invoice?.tot_tva_invoice}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$HT ${commandsLine[0].invoice?.prix_article_tot}", 400f, startY, paint)
        startY += 20f
        canvas?.drawText("$TTC ${commandsLine[0].invoice?.prix_invoice_tot}", 400f, startY, paint)

        pdfDocument.finishPage(page)

        // Save PDF
        val pdfFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), FILE_NAME)
        if (!pdfFolder.exists()) pdfFolder.mkdirs()
        val outputFile = File(pdfFolder, "${commandsLine[0].invoice?.code}.pdf")
        val fileOutputStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(fileOutputStream)
        pdfDocument.close()

        Toast.makeText(context, PDF_GENERATED, Toast.LENGTH_SHORT).show()

    } catch (e: IOException) {
        Log.e("PDF", "Error generating PDF: ${e.message}")
        Toast.makeText(context, ERROR_PDF, Toast.LENGTH_SHORT).show()
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