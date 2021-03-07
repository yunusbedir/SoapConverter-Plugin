package model

data class RootDC(
    var classNumber: String? = "0.0",
    var baseClassName: String?,
    var className: String?,
    var tagName: String?,
    var elements: ArrayList<ElementDC>?,
    var nameSpaces: ArrayList<NamespaceDC>?
)