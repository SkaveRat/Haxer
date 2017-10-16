package net.skaverat.haxler.amazon

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sqs.AmazonSQSClient

class AmazonClient {
    fun getCredentials(): AWSCredentials? {
        var credentials: AWSCredentials? = null
        try {
            credentials = ProfileCredentialsProvider().credentials
        } catch (e: Exception) {
            throw AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e)
        }

        return credentials
    }


    fun getAmazonSQSClient(): AmazonSQSClient {
        val sqsClient = AmazonSQSClient(getCredentials())
        sqsClient.setRegion(Region.getRegion(Regions.US_EAST_1))
        return sqsClient
    }

    fun getAmazonS3Client(): AmazonS3Client {
        val s3Client = AmazonS3Client(getCredentials())
        val region = Region.getRegion(Regions.US_EAST_1)
        s3Client.setRegion(region)
        return s3Client
    }

    fun getAmazonEC2Client(): AmazonEC2Client {
        val ec2 = AmazonEC2Client(getCredentials())
        val usWest2 = Region.getRegion(Regions.US_EAST_1)
        ec2.setRegion(usWest2)
        return ec2
    }
}