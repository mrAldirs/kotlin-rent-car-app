package com.example.rental_mobil.Utils

import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPageEvent
import com.itextpdf.text.pdf.PdfWriter


class PageNumeration : PdfPageEvent {
    private val TAG = PageNumeration::class.java.simpleName
    private val FONT_FOOTER: Font =
        Font(Font.FontFamily.TIMES_ROMAN, 8f, Font.NORMAL, BaseColor.DARK_GRAY)

    fun PageNumeration() {}
    override fun onOpenDocument(writer: PdfWriter?, document: Document?) {

    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {

    }

    override fun onEndPage(writer: PdfWriter, document: Document) {
        try {
            var cell: PdfPCell
            val table = PdfPTable(2)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(3f, 1f))

            //1st Column
            val anchor = Anchor(Phrase("", FONT_FOOTER))
            anchor.setReference("http://mywebsite.com/")
            cell = PdfPCell(anchor)
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = 0
            cell.setPadding(2f)
            table.addCell(cell)
            table.setTotalWidth(
                document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
            )
            table.writeSelectedRows(
                0,
                -1,
                document.leftMargin(),
                document.bottomMargin() - 5,
                writer.directContent
            )

            //2nd Column
            cell = PdfPCell(Phrase("Page - " + writer.pageNumber.toString(), FONT_FOOTER))
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = 0
            cell.setPadding(2f)
            table.addCell(cell)
            table.setTotalWidth(
                document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
            )
            table.writeSelectedRows(
                0,
                -1,
                document.leftMargin(),
                document.bottomMargin() - 5,
                writer.directContent
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e(TAG, ex.toString())
        }
    }

    override fun onCloseDocument(writer: PdfWriter?, document: Document?) {

    }

    override fun onParagraph(writer: PdfWriter?, document: Document?, paragraphPosition: Float) {

    }

    override fun onParagraphEnd(writer: PdfWriter?, document: Document?, paragraphPosition: Float) {

    }

    override fun onChapter(
        writer: PdfWriter?,
        document: Document?,
        paragraphPosition: Float,
        title: Paragraph?
    ) {

    }

    override fun onChapterEnd(writer: PdfWriter?, document: Document?, paragraphPosition: Float) {

    }

    override fun onSection(
        writer: PdfWriter?,
        document: Document?,
        paragraphPosition: Float,
        depth: Int,
        title: Paragraph?
    ) {

    }

    override fun onSectionEnd(writer: PdfWriter?, document: Document?, paragraphPosition: Float) {

    }

    override fun onGenericTag(
        writer: PdfWriter?,
        document: Document?,
        rect: Rectangle?,
        text: String?
    ) {

    }
}