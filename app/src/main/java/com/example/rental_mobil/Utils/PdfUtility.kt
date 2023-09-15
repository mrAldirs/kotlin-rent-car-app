package com.example.rental_mobil.Utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.example.rental_mobil.R
import com.itextpdf.text.*
import com.itextpdf.text.pdf.Barcode128
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.List

  object PdfUtility {
    private val TAG: String = PdfUtility::class.java.simpleName
    private val FONT_TITLE: Font = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    private val FONT_SUBTITLE: Font = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL)

    private val FONT_CELL: Font = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private val FONT_COLUMN: Font = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.NORMAL)

    interface OnDocumentClose {
        fun onPDFDocumentClose(file: File?)
    }

    @Throws(Exception::class)
    fun createPdf(
        @NonNull mContext: Context,
        mCallback: OnDocumentClose?,
        items: MutableList<Array<String>>,
        @NonNull filePath: String,
        isPortrait: Boolean
    ) {
        if (filePath == "") {
            throw NullPointerException("PDF File Name can't be null or blank. PDF File can't be created")
        }
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
            Thread.sleep(50)
        }
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.setPageSize(if (isPortrait) PageSize.A4 else PageSize.A4.rotate())
        val pdfWriter: PdfWriter = PdfWriter.getInstance(document, FileOutputStream(filePath))
        pdfWriter.setFullCompression()
        pdfWriter.setPageEvent(PageNumeration())
        document.open()
        setMetaData(document)
        addHeader(mContext, document)
        addEmptyLine(document, 2)
        document.add(createDataTable(items))
        addEmptyLine(document, 2)
        document.add(createSignBox(items))
        document.close()
        try {
            pdfWriter.close()
        } catch (ex: Exception) {
            Log.e(TAG, "Error While Closing pdfWriter : $ex")
        }
        mCallback?.onPDFDocumentClose(file)
    }

    @Throws(DocumentException::class)
    private fun addEmptyLine(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun setMetaData(document: Document) {
        document.addCreationDate()
        //document.add(new Meta("",""));
        document.addAuthor("RAVEESH G S")
        document.addCreator("RAVEESH G S")
        document.addHeader("DEVELOPER", "RAVEESH G S")
    }

    @Throws(Exception::class)
    private fun addHeader(mContext: Context, document: Document) {
        val table = PdfPTable(3)
        table.widthPercentage =100f
        table.setWidths(floatArrayOf(2f, 7f, 2f))
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER)
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER)
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
        var cell: PdfPCell
        run {

            /*MIDDLE TEXT*/cell = PdfPCell()
            cell.setHorizontalAlignment(Element.ALIGN_CENTER)
            cell.setBorder(PdfPCell.NO_BORDER)
            cell.setPadding(8f)
            cell.setUseAscender(true)
            var temp = Paragraph("I AM TITLE", FONT_TITLE)
            temp.setAlignment(Element.ALIGN_CENTER)
            cell.addElement(temp)
            temp = Paragraph("I am Subtitle", FONT_SUBTITLE)
            temp.setAlignment(Element.ALIGN_CENTER)
            cell.addElement(temp)
            table.addCell(cell)
        }
        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table)
    }

    @Throws(DocumentException::class)
    private fun createDataTable(dataTable: MutableList<Array<String>>): PdfPTable? {
        val table1 = PdfPTable(7)
        table1.setWidthPercentage(100f)
        table1.setWidths(floatArrayOf(
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
        ))
        table1.setHeaderRows(1)
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER)
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
        var cell: PdfPCell
        val nama_penyewa="Nama penyewa"
        val plat_mobil="Plat mobil"
        val mulai_rental="Sewa"
        val selesai_rental="Kembali"
        val harga_sewa="Harga Sewa"
        val harga_denda="Denda"
        val total="Total"
        run {
            table1.addCell(pdfCellHeader(nama_penyewa))
            table1.addCell(pdfCellHeader(plat_mobil))
            table1.addCell(pdfCellHeader(mulai_rental))
            table1.addCell(pdfCellHeader(selesai_rental))
            table1.addCell(pdfCellHeader(harga_sewa))
            table1.addCell(pdfCellHeader(harga_denda))
            table1.addCell(pdfCellHeader(total))
        }
        val top_bottom_Padding = 8f
        val left_right_Padding = 4f
        var alternate = false
        val lt_gray = BaseColor(221, 221, 221) //#DDDDDD
        var cell_color: BaseColor
        val size = dataTable.size
        for (i in 0 until size) {
            cell_color = if (alternate) lt_gray else BaseColor.WHITE
            val temp = dataTable[i]
            table1.addCell(pdfCellItems(temp[0],cell_color))
            table1.addCell(pdfCellItems(temp[1],cell_color))
            table1.addCell(pdfCellItems(temp[2],cell_color))
            table1.addCell(pdfCellItems(temp[3],cell_color))
            table1.addCell(pdfCellItems(temp[4],cell_color))
            table1.addCell(pdfCellItems(temp[5],cell_color))
            table1.addCell(pdfCellItems(temp[6],cell_color))
            alternate = !alternate
        }
        return table1
    }
    private fun pdfCellHeader(value:String):PdfPCell{
        var cell = PdfPCell(Phrase(value, FONT_COLUMN))
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
        cell.setPadding(4f)
        return cell
    }
      private fun pdfCellItems(value:String,cell_color:BaseColor):PdfPCell{
          val top_bottom_Padding = 8f
          val left_right_Padding = 4f
          val lt_gray = BaseColor(221, 221, 221) //#DDDDDD

         var cell = PdfPCell(Phrase(value, FONT_CELL))
          cell.setHorizontalAlignment(Element.ALIGN_CENTER)
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
          cell.setPaddingLeft(left_right_Padding)
          cell.setPaddingRight(left_right_Padding)
          cell.setPaddingTop(top_bottom_Padding)
          cell.setPaddingBottom(top_bottom_Padding)
          cell.setBackgroundColor(cell_color)
        return cell
    }
    @Throws(DocumentException::class)
    private fun createSignBox(dataTable: MutableList<Array<String>>): PdfPTable? {
        val outerTable = PdfPTable(1)
        outerTable.setWidthPercentage(100f)
        outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER)
        val innerTable = PdfPTable(4)
        run {
            innerTable.setWidthPercentage(100f)
            innerTable.setWidths(floatArrayOf(
                1f,
                2f,
                2f,
                2f,
            ))

            innerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER)

            //ROW-1 : EMPTY SPACE
            var cell = PdfPCell()
            cell.setBorder(PdfPCell.NO_BORDER)
            cell.setFixedHeight(60f)
            innerTable.addCell(cell)

            //ROW-2 : EMPTY SPACE
            cell = PdfPCell()
            cell.setBorder(PdfPCell.NO_BORDER)
            cell.setFixedHeight(60f)
            innerTable.addCell(cell)

            //ROW-3 : EMPTY SPACE
            cell = PdfPCell()
            cell.setBorder(PdfPCell.NO_BORDER)
            cell.setFixedHeight(60f)
            innerTable.addCell(cell)
            var total =0
            for (item in dataTable) {
                total+=item[6].toInt()
            }
            val totalText = "Total Keseluruhan: Rp.$total"
            //ROW-4 : Content Right Aligned
            cell = PdfPCell(Phrase(totalText, FONT_SUBTITLE))

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
            cell.setBorder(PdfPCell.NO_BORDER)
            cell.setPadding(4f)
            innerTable.addCell(cell)
        }
        val signRow = PdfPCell(innerTable)
        signRow.setHorizontalAlignment(Element.ALIGN_LEFT)
        signRow.setBorder(PdfPCell.NO_BORDER)
        signRow.setPadding(4f)
        outerTable.addCell(signRow)
        return outerTable
    }

    @Throws(Exception::class)
    private fun getImage(imageByte: ByteArray, isTintingRequired: Boolean): Image? {
        val paint = Paint()
        if (isTintingRequired) {
            paint.setColorFilter(PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN))
        }
        val input = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        val output = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(input, 0f, 0f, paint)
        val stream = ByteArrayOutputStream()
        output.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image: Image = Image.getInstance(stream.toByteArray())
        image.setWidthPercentage(80f)
        return image
    }

    private fun getBarcodeImage(pdfWriter: PdfWriter, barcodeText: String): Image? {
        val barcode = Barcode128()
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.setFont(null)
        barcode.setCode(barcodeText)
        barcode.setCodeType(Barcode128.CODE128)
        barcode.setTextAlignment(Element.ALIGN_BASELINE)
        return barcode.createImageWithBarcode(pdfWriter.getDirectContent(), BaseColor.BLACK, null)
    }
}