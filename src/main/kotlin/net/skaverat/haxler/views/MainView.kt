package net.skaverat.haxler.views

import net.skaverat.haxler.Styles
import org.apache.log4j.LogManager
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    private val logger = LogManager.getLogger(MainView::class.java)

//    val mapper = ObjectMapper().registerKotlinModule()

//    fun foo(){
//        logger.info(.readValue(File("test.json")))
//    }


    override val root = borderpane {
//        foo()
        addClass(Styles.welcomeScreen)

        top {
            tabpane {
                tab(Projects::class)
                tab(Overview::class)
            }
        }

    }
}