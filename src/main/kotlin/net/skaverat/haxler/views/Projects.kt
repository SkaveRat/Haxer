package net.skaverat.haxler.views

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import net.skaverat.haxler.models.Project
import org.apache.log4j.LogManager
import tornadofx.*
import java.io.File

class Projects : View("Projects") {
    private val logger = LogManager.getLogger(Projects::class.java)

    override val closeable = SimpleBooleanProperty(false)

    val jsonMapper = jacksonObjectMapper()

    var projectTable: TableView<Project> by singleAssign()
    var nameField: TextField by singleAssign()
    var filepathField: TextField by singleAssign()

    var projects = mutableListOf<Project>().observable()

    override val root = hbox {
        projects.addAll(jsonMapper.readValue<List<Project>>(File("test.json")))
        val model = ProjectModel(projects.first())


        fun save() {
            model.commit()
            saveProjectsToFile()
        }

        vbox {
            tableview(projects) {
                projectTable = this
                column("Name", Project::nameProperty)

                model.rebindOnChange(this) { selectedProject ->
                    save()
                    project = selectedProject ?: projects.first()
                }

                columnResizePolicy = SmartResize.POLICY
            }

            hbox {
                button("+") {
                    action {
                        projects.add(Project("New Project", ""))
                        saveProjectsToFile()
                    }
                }
                button("-") {
                    action {
                        if (projects.size <= 1) {
                            projects.add(Project("New Project", ""))
                        }
                        projects.removeAt(projectTable.selectionModel.selectedIndex)
                        saveProjectsToFile()
                    }
                }

            }

        }



        form {
            vbox {
                fieldset {
                    field("Name: ") {
                        textfield(model.name) {
                            nameField = this
                        }
                    }
                }

                fieldset {
                    field("File:") {

                        textfield(model.filepath) {
                            filepathField = this
                        }
                        button("Select file") {
                            action {
                                val files = chooseFile("File?", arrayOf(FileChooser.ExtensionFilter("Blender", listOf("*.blend"))))
                                if (files.isNotEmpty()) {
                                    filepathField.clear()
                                    filepathField.insertText(0, files[0].absolutePath)
                                }
                            }
                        }
                    }
                }
                hbox {
                    button("Save") {
                        action { save() }
                    }
                    button("Reset") {
                        action { model.rollback() }
                    }

                }
            }
        }
    }

    fun saveProjectsToFile() {
        File("test.json").writeText(jsonMapper.writeValueAsString(projects))
    }

    class ProjectModel(var project: Project) : ItemViewModel<Project>() {
        val id = bind { project.idProperty }
        val name = bind { project.nameProperty }
        val filepath = bind { project.filepathProperty }
    }

}
