package com.nutrigo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("status_verbose")
	val statusVerbose: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Nutriments(

	// Energi
	@field:SerializedName("energy")
	val energy: Double? = null,

	@field:SerializedName("energy_unit")
	val energyUnit: String? = null,

	@field:SerializedName("energy_value")
	val energyValue: Double? = null,

	@field:SerializedName("energy_100g")
	val energy100g: Double? = null,

	@field:SerializedName("energy_serving")
	val energyServing: Double? = null,

	@field:SerializedName("energy-kcal")
	val energyKcal: Double? = null,

	@field:SerializedName("energy-kcal_100g")
	val energyKcal100g: Double? = null,

	@field:SerializedName("energy-kcal_serving")
	val energyKcalServing: Double? = null,

	@field:SerializedName("energy-kcal_value")
	val energyKcalValue: Double? = null,

	@field:SerializedName("energy-kcal_value_computed")
	val energyKcalValueComputed: Double? = null,

	@field:SerializedName("energy-kcal_unit")
	val energyKcalUnit: String? = null,

	// Lemak
	@field:SerializedName("fat")
	val fat: Double? = null,

	@field:SerializedName("fat_unit")
	val fatUnit: String? = null,

	@field:SerializedName("fat_value")
	val fatValue: Double? = null,

	@field:SerializedName("fat_100g")
	val fat100g: Double? = null,

	@field:SerializedName("fat_serving")
	val fatServing: Double? = null,

	@field:SerializedName("saturated-fat")
	val saturatedFat: Double? = null,

	@field:SerializedName("saturated-fat_unit")
	val saturatedFatUnit: String? = null,

	@field:SerializedName("saturated-fat_value")
	val saturatedFatValue: Double? = null,

	@field:SerializedName("saturated-fat_100g")
	val saturatedFat100g: Double? = null,

	@field:SerializedName("saturated-fat_serving")
	val saturatedFatServing: Double? = null,

	// Karbohidrat
	@field:SerializedName("carbohydrates")
	val carbohydrates: Double? = null,

	@field:SerializedName("carbohydrates_unit")
	val carbohydratesUnit: String? = null,

	@field:SerializedName("carbohydrates_value")
	val carbohydratesValue: Double? = null,

	@field:SerializedName("carbohydrates_100g")
	val carbohydrates100g: Double? = null,

	@field:SerializedName("carbohydrates_serving")
	val carbohydratesServing: Double? = null,

	// Gula
	@field:SerializedName("sugars")
	val sugars: Double? = null,

	@field:SerializedName("sugars_unit")
	val sugarsUnit: String? = null,

	@field:SerializedName("sugars_value")
	val sugarsValue: Double? = null,

	@field:SerializedName("sugars_100g")
	val sugars100g: Double? = null,

	@field:SerializedName("sugars_serving")
	val sugarsServing: Double? = null,

	// Protein
	@field:SerializedName("proteins")
	val proteins: Double? = null,

	@field:SerializedName("proteins_unit")
	val proteinsUnit: String? = null,

	@field:SerializedName("proteins_value")
	val proteinsValue: Double? = null,

	@field:SerializedName("proteins_100g")
	val proteins100g: Double? = null,

	@field:SerializedName("proteins_serving")
	val proteinsServing: Double? = null,

	// Garam
	@field:SerializedName("salt")
	val salt: Double? = null,

	@field:SerializedName("salt_unit")
	val saltUnit: String? = null,

	@field:SerializedName("salt_value")
	val saltValue: Double? = null,

	@field:SerializedName("salt_100g")
	val salt100g: Double? = null,

	@field:SerializedName("salt_serving")
	val saltServing: Double? = null,

	// Lainnya
	@field:SerializedName("fruits-vegetables-legumes-estimate-from-ingredients_100g")
	val fruitsVegetablesLegumesEstimateFromIngredients100g: Double? = null,

	@field:SerializedName("fruits-vegetables-legumes-estimate-from-ingredients_serving")
	val fruitsVegetablesLegumesEstimateFromIngredientsServing: Double? = null,

	@field:SerializedName("fruits-vegetables-nuts-estimate-from-ingredients_100g")
	val fruitsVegetablesNutsEstimateFromIngredients100g: Double? = null,

	@field:SerializedName("fruits-vegetables-nuts-estimate-from-ingredients_serving")
	val fruitsVegetablesNutsEstimateFromIngredientsServing: Double? = null
)


data class Product(

	@field:SerializedName("nutriments")
	val nutriments: Nutriments? = null
)
