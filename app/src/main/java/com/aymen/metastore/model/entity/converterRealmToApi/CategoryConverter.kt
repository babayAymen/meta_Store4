package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.room.Category
import com.aymen.metastore.model.entity.room.SubCategory
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.entity.dto.SubCategoryDto

fun mapCategoryToRoomCategory(cat : CategoryDto): Category{
    return Category(
        id = cat.id,
         code =  cat.code,
         libelle = cat.libelle,
         image = cat.image,
    )
}

fun mapRoomCategoryToCategory(cat : Category): CategoryDto{
    return CategoryDto(
        id = cat.id,
        code =  cat.code,
        libelle = cat.libelle,
        image = cat.image,
    )
}

fun mapSubCategoryToRoomSubCategory(sub : SubCategoryDto): SubCategory{
    return SubCategory(
        id = sub.id,
        code = sub.code,
         libelle = sub.libelle,
         image = sub.image,
         categoryId = sub.category.id,
    )
}

fun mapRoomSubCategoryToSubCategory(sub : SubCategory):SubCategoryDto{
    return SubCategoryDto(
        id = sub.id,
        code = sub.code,
        libelle = sub.libelle,
        image = sub.image,
    )
}