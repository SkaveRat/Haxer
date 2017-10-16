package net.skaverat.haxler.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.util.*

@JsonInclude()
class Project(name: String, filepath: String) {

    val id: UUID = UUID.randomUUID()
    @JsonIgnore
    val idProperty = SimpleStringProperty(this, "id", id.toString())

    @JsonIgnore
    val nameProperty = SimpleStringProperty(this, "name", name)
    var name by nameProperty


    @JsonIgnore
    val filepathProperty = SimpleStringProperty(this, "filepath", filepath)
    var filepath by filepathProperty


}