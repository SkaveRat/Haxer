package net.skaverat.haxler.views

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import net.skaverat.haxler.amazon.AmazonClient
import net.skaverat.haxler.models.Project
import net.skaverat.haxler.models.RenderData
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
    var splitpartsField: TextField by singleAssign()
    var resolutionField: TextField by singleAssign()

    val amazonClient = AmazonClient()


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
                    fieldset {
                        field("Parts:") {
                            textfield(model.splitparts) {
                                splitpartsField = this
                            }
                        }
                    }

                    fieldset {
                        field("Resolution:") {
                            textfield(model.resolution) {
                                resolutionField = this
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
                hbox {
                    button("Upload") {
                        action { upload() }
                    }
                    button("Foobar") {
                        action{
                            val s3client = amazonClient.getAmazonS3Client()
                            var res = s3client.listObjects("haxler")
                            res.objectSummaries.forEach { obj ->
                                logger.info(obj.key)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun upload() {
        var sqsClient = amazonClient.getAmazonSQSClient()
        val queueUrl = sqsClient.listQueues("haxler").getQueueUrls().get(0)
        val renderData = RenderData()
        renderData.projectName = "foobar"
        renderData.frame = "1"
        renderData.use_stereo = false
        val partsX = 2
        val partsY = 1

        for(y in 0 until  partsY) {
            for(x in 0 until partsX) {
                renderData.useParts = true
                renderData.partsNum = partsX*y+x
                renderData.partsMinX = (x*(1.0f/partsX))
                renderData.partsMaxX = ((x+1)*(1.0f/partsX))
                renderData.partsMinY = (y*(1.0f/partsY))
                renderData.partsMaxY = ((y+1)*(1.0f/partsY))
                sqsClient.sendMessage(queueUrl, jsonMapper.writeValueAsString(renderData))
                logger.info(jsonMapper.writeValueAsString(renderData))
            }
        }
    }


    private fun saveProjectsToFile() {
        File("test.json").writeText(jsonMapper.writeValueAsString(projects))
    }

    class ProjectModel(var project: Project) : ItemViewModel<Project>() {
        val id = bind { project.idProperty }
        val name = bind { project.nameProperty }
        val filepath = bind { project.filepathProperty }
        val splitparts = bind { project.splitpartsProperty }
        val resolution = bind { project.resolutionProperty }
    }

}
