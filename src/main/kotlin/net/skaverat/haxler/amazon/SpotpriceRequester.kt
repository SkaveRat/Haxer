package net.skaverat.haxler.amazon

import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest
import com.amazonaws.services.ec2.model.InstanceType
import com.amazonaws.services.ec2.model.SpotPrice
import java.util.*
import java.util.Arrays.asList

class SpotpriceRequester {

    private val amazonClient: AmazonClient = AmazonClient()


    fun getCurrentPrice(instanceType: InstanceType): List<SpotPrice> {
        val ec2 = amazonClient.getAmazonEC2Client()
        val request = getDescribeSpotPriceHistoryRequest(instanceType)
        val priceHistory = ec2.describeSpotPriceHistory(request)

        val result = ArrayList<SpotPrice>()
        val foundPrices = ArrayList<String>()
        for (spotPrice in priceHistory.spotPriceHistory) {
            if (foundPrices.contains(spotPrice.availabilityZone)) {
                continue
            }

            result.add(spotPrice)
            foundPrices.add(spotPrice.availabilityZone)
        }

        return result
    }

    private fun getDescribeSpotPriceHistoryRequest(instanceType: InstanceType): DescribeSpotPriceHistoryRequest {
        val request = DescribeSpotPriceHistoryRequest()
        request.setInstanceTypes(asList<String>(instanceType.toString()))
        request.setProductDescriptions(asList<String>("Linux/UNIX"))
        request.maxResults = 20
        return request
    }

}