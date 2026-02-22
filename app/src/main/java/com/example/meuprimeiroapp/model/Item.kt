package com.example.meuprimeiroapp.model

/**
 * Represents a basic item with an identifier.
 *
 * @property id Unique identifier for the item.
 */
data class Item(
    val id: String,
    val value: ItemValue
)

/**
 * Represents the detailed information of an item.
 *
 * @property id Unique identifier for the item.
 * @property name The first name of the person.
 * @property surname The last name of the person.
 * @property profession The occupation of the person.
 * @property imageUrl URL pointing to the person's profile image.
 * @property age The age of the person in years.
 * @property location Geographical location details associated with the item.
 */
data class ItemValue(
    val id: String,
    val name: String,
    val surname: String,
    val profession: String,
    val imageUrl: String,
    val age: Int,
    val location: ItemLocation
)

/**
 * Represents geographical location details.
 *
 * @property name The name or address of the location.
 * @property latitude The latitude coordinate of the location.
 * @property longitude The longitude coordinate of the location.
 */
data class ItemLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
