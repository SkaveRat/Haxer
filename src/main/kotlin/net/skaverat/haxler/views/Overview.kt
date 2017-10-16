package net.skaverat.haxler.views

import com.amazonaws.services.ec2.model.InstanceType
import com.amazonaws.services.ec2.model.SpotPrice
import javafx.beans.property.SimpleBooleanProperty
import net.skaverat.haxler.amazon.SpotpriceRequester
import org.apache.log4j.LogManager
import tornadofx.*

class Overview : View("My View") {
    override val closeable = SimpleBooleanProperty(false)

    private val logger = LogManager.getLogger(Overview::class.java)

    private val spotprice: SpotpriceRequester = SpotpriceRequester();


    override val root = vbox {

        val cachedPrices = mutableListOf<SpotPrice>().observable()

        tableview(cachedPrices) {
            column("#", SpotPrice::getInstanceType)
            column("Zone", SpotPrice::getAvailabilityZone)
            column("Price", SpotPrice::getSpotPrice)
            columnResizePolicy = SmartResize.POLICY
        }

        button("Foobarbutton") {
            action {
                runAsync {
                    spotprice.getCurrentPrice(InstanceType.C1Medium)
                } ui { prices ->
                    cachedPrices.addAll(prices)
                }
            }
        }


    }
}
