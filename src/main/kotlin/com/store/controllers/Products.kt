package com.store.controllers

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

data class Product(
    val id: Int,
    val name: String,
    val type: String,
    val inventory: Int
)

data class ProductId(
    val id: Int
)

@RestController
@RequestMapping("/products")
class Products {

    private val products = mutableListOf<Product>()
    private var nextId = 1

    private val validTypes = setOf("book", "food", "gadget", "other")

    @GetMapping
    fun getProducts(
        @RequestParam(required = false) type: String?
    ): List<Product> {

        if (type != null && type !in validTypes) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid product type"
            )
        }

        return if (type == null) {
            products
        } else {
            products.filter { it.type == type }
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @RequestBody body: JsonNode
    ): ProductId {

        // name must exist and must be a JSON string
        if (!body.has("name") || !body.get("name").isTextual) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Product name is required and must be a string"
            )
        }

        // type must exist and must be a JSON string
        if (!body.has("type") || !body.get("type").isTextual) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Product type is required and must be a string"
            )
        }

        // inventory must exist and must be a JSON integer
        if (!body.has("inventory") || !body.get("inventory").isInt) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Inventory must be an integer"
            )
        }
	
	// cost is optional, but when present it must be a JSON number
        if (body.has("cost") && !body.get("cost").isNumber) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cost must be a number"
            )
         }

        val name = body.get("name").asText()
        val type = body.get("type").asText()
        val inventory = body.get("inventory").asInt()

        if (name.isBlank()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Product name is required"
            )
        }

        if (type !in validTypes) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid product type"
            )
        }

        if (inventory !in 1..9999) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Inventory must be between 1 and 9999"
            )
        }

        val product = Product(
            id = nextId++,
            name = name,
            type = type,
            inventory = inventory
        )

        products.add(product)

        return ProductId(product.id)
    }
}