package common

import model.ElementDC
import model.NamespaceDC
import model.OutDataClass
import model.RootDC

object Converter {

    fun List<RootDC>.convertToOutDataClass(): List<OutDataClass> = map { rootDC ->
        OutDataClass(rootDC.className!!, rootDC.convertToString())
    }

    private fun RootDC.convertToString(): String {
        var result = ""
        try {
            //NAMESPACES
            if (this.nameSpaces!!.size > 0)
                result += "\n@NamespaceList(" + this.nameSpaces!!.joinToString {
                    "\n\tNamespace(prefix = \"${it.prefix}\" , reference = \"${it.reference}\")"
                } + "\n)\n"
        } catch (e: Exception) {
        }

        try {
            //ROOT DATA CLASS
            result += "@Root(name = \"${this.tagName}\")\n"
            result += "public data class ${this.className}(\n\n"
        } catch (e: Exception) {

        }
        try {
            //ELEMENTS
            this.elements!!.forEach {
                if (it.type!!.contains("ArrayList")) {
                    result += "\t@field:ElementList(inline = true , required = false)\n"
                    result += "\tpublic ${it.elementName}s : ${it.type}\n\n"

                } else {
                    result += "\t@field:Element(name = \"${it.tagName}\" , required = false)\n"
                    result += "\tpublic ${it.elementName} : ${it.type}\n\n"
                }
            }
            result += ")\n\n"
        } catch (e: Exception) {

        }
        return result
    }

    fun String.convertToRootDC(rootName: String): ArrayList<RootDC> {
        val listDataClasses = ArrayList<RootDC>()
        listDataClasses.add(RootDC("0.0", rootName, "", "",arrayListOf<ElementDC>(), arrayListOf()))
        var tabNumber = 0
        var classNumberInTabNumber: String
        var baseClassName: String
        var elements = ArrayList<ElementDC>()
        lines().forEach {
            var dataClassNameSpaces = ArrayList<NamespaceDC>()
            var line = it
            if (line.contains("<!--").not()) {
                if (line.contains("xmlns")) {
                    val firstIndex = line.indexOf("xmlns")
                    dataClassNameSpaces = getNameSpaces(line)
                    line = line.substring(0, firstIndex)
                }
                if (line.replace(" ", "").substring(0, 2) == "</") {
                    tabNumber--
                } else if (line.contains("<") && (line.contains("/>") || line.contains("</"))) {
                    val elementName = getElementName(line)
                    val tagName = getTagName(line)
                    val elementType = "String"
                    elements.add(ElementDC(elementName,tagName, elementType))
                } else if (line.contains("<")) {
                    var isContinue = false
                    val last = if (tabNumber != 0) {
                        listDataClasses.last { dataClass ->
                            dataClass.classNumber!!.split(".")[0] == (tabNumber - 1).toString()
                        }
                    } else {
                        null
                    }

                    baseClassName = try {
                        last?.className.toString()
                    } catch (e: Exception) {
                        "Base"
                    }
                    var elementName = getElementName(line)
                    val tagName = getTagName(line)
                    val elementType = if (line.contains("item")) {
                        elementName = "${baseClassName.decapitalize()}${elementName.capitalize()}"
                        "ArrayList<${elementName.capitalize()}>"
                    } else {
                        elementName
                    }.capitalize()

                    try {
                        if (last?.elements?.any { element -> element.elementName == elementName }!!) {
                            elements = arrayListOf()
                            isContinue = true
                        }
                    } catch (e: java.lang.Exception) {

                    }
                    if (isContinue.not()) {
                        try {
                            last!!.elements?.add(ElementDC(elementName,tagName, elementType))
                        } catch (e: java.lang.Exception) {
                            elements.add(ElementDC(elementName,tagName, elementType))
                        }
                        classNumberInTabNumber = try {
                            listDataClasses.filter { dataClass ->
                                dataClass.classNumber!!.split(".")[0] == tabNumber.toString()
                            }.size.toString()
                        } catch (e: Exception) {
                            "0"
                        }
                        listDataClasses.last().apply {
                            this.classNumber = "$tabNumber.$classNumberInTabNumber"
                            this.baseClassName = baseClassName
                            this.className = elementName.capitalize()
                            this.tagName = tagName
                            this.elements = arrayListOf()
                            this.nameSpaces = dataClassNameSpaces
                        }
                        try {
                            listDataClasses[listDataClasses.lastIndex - 1].elements!!.addAll(
                                elements
                            )
                        } catch (e: java.lang.Exception) {

                        }
                        listDataClasses.add(RootDC("-1.0", null, null, null, null,null))
                        baseClassName = elementName
                        elements = ArrayList()
                    }
                    tabNumber++
                }
            }
        }
        listDataClasses[listDataClasses.lastIndex - 1].elements!!.addAll(elements)
        listDataClasses.removeAt(listDataClasses.lastIndex)
        return listDataClasses
    }

    private fun getTagName(line: String): String {
        if (line.contains("<") && (line.contains("/>") || line.contains("</"))) {
            var lastIndex = line.indexOf("/>")
            if (lastIndex <= 0)
                lastIndex = line.substring(0, line.indexOf("</") + 3).indexOf(">")

            return line.substring(line.indexOf("<") + 1, lastIndex).replace(" ", "")
        } else if (line.contains("<")) {
            var lastIndex = line.indexOf(">")
            if (lastIndex <= 0)
                lastIndex = line.length
            return line.substring(line.indexOf("<") + 1, lastIndex)
        }
        return ""
    }

    private fun getNameSpaces(line: String): ArrayList<NamespaceDC> {
        val firstIndex = line.indexOf("xmlns")
        val xmlns = line.substring(firstIndex, line.length)
        val dataClassNameSpaces = ArrayList<NamespaceDC>()
        xmlns.split("xmlns:").forEach { xml ->
            if (xml.isNotBlank()) {
                val prefix = xml.substring(0, xml.indexOf("="))
                val reference = xml.substring(xml.indexOf("=") + 2, xml.lastIndex - 1)
                dataClassNameSpaces.add(NamespaceDC(prefix = prefix, reference = reference))
            }
        }
        return dataClassNameSpaces
    }

    private fun getElementName(line: String): String {
        return getTagName(line).replace(":","").decapitalize()
    }
}
