package ru.tensor.sbis.edo.additional_fields.decl.model.value

/** @SelfDocumented */
sealed class FaceListType {

    object Person : FaceListType() {
        override fun toString(): String = "FaceListType.Person"
    }

    object Contractor : FaceListType() {
        override fun toString(): String = "FaceListType.Contractor"
    }
}
