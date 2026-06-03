package com.example.transactions.mlkit

import org.junit.Assert.assertEquals
import org.junit.Test

class ReceiptParserTest {

    @Test
    fun `parseReceipt picks integer total with currency instead of decimal tax`() {
        val result = parseReceipt(
            """
            FRESH MART
            TAX 375.00
            TOTAL ₦5,500
            CASH ₦6,000
            CHANGE ₦500
            """.trimIndent()
        )

        assertEquals(5500.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks whole number on line after total label`() {
        val result = parseReceipt(
            """
            CORNER SHOP
            ITEM 1 1200
            AMOUNT DUE
            5500
            PAID
            6000
            """.trimIndent()
        )

        assertEquals(5500.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks number below total after standalone currency symbol`() {
        val result = parseReceipt(
            """
            CORNER SHOP
            ITEM 1 1200
            TOTAL
            ₦
            5500
            PAID
            6000
            """.trimIndent()
        )

        assertEquals(5500.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks number beside total without currency symbol`() {
        val result = parseReceipt(
            """
            CORNER SHOP
            ITEM 1 1200
            TOTAL 5500
            PAID 6000
            """.trimIndent()
        )

        assertEquals(5500.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks one decimal number beside total`() {
        val result = parseReceipt(
            """
            SHOP NAME
            Lorem 1.1
            Ipsum 2.2
            Dolor sit amet 3.3
            Consectetur 4.4
            Adipiscing elit 5.5
            Total 16.5
            Cash 20.0
            Change 3.5
            """.trimIndent()
        )

        assertEquals(16.5, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks one decimal number below total`() {
        val result = parseReceipt(
            """
            SHOP NAME
            Lorem 1.1
            Ipsum 2.2
            Total
            16.5
            Cash
            20.0
            """.trimIndent()
        )

        assertEquals(16.5, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt picks total from column ordered OCR text`() {
        val result = parseReceipt(
            """
            Ipsum
            Address: Lorem Ipsum, 23-10
            Telp. 11223344
            SHOP NAME
            Description
            Lorem
            CASH RECEIPT
            Dolor sit amet
            Consectetur
            Total
            Adipiscing elit
            Cash
            Change
            Bank card
            Approval Code
            THANK YOU!
            designed by freepik
            Price
            1.1
            2.2
            3.3
            4,4
            5.5
            k**
            16.5
            20.0
            3.5
            ** *
            234
            #123456
            """.trimIndent()
        )

        assertEquals(16.5, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt prefers final total over subtotal and tax`() {
        val result = parseReceipt(
            """
            GROCERY HOUSE
            SUBTOTAL 4,950.00
            VAT 371.25
            GRAND TOTAL NGN 5,321
            """.trimIndent()
        )

        assertEquals(5321.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt uses last amount on total line`() {
        val result = parseReceipt(
            """
            MINI MARKET
            2 ITEMS
            TOTAL ITEMS 2 TOTAL 1,850.00
            """.trimIndent()
        )

        assertEquals(1850.0, result.amount ?: 0.0, 0.001)
    }

    @Test
    fun `parseReceipt ignores same line cash amount after total`() {
        val result = parseReceipt(
            """
            FOOD STOP
            TOTAL 5500 CASH 6000 CHANGE 500
            """.trimIndent()
        )

        assertEquals(5500.0, result.amount ?: 0.0, 0.001)
    }
}
