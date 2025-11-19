package bz.sample

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val port = 11323
    //Create DatagramSocket with port #
    DatagramSocket(port).use { socket ->

        println("Listening on port ${socket.localPort}")

        // Construct DatagramPacket with a buffer size of 1024 bytes
        val buffer = ByteArray(1024)
        val receivedBuffer = DatagramPacket(buffer, buffer.size)

        while (true) {
            // read data from socket into received buffer
            socket.receive(receivedBuffer)

            // convert byte to string
            val receivedMessage = String(receivedBuffer.data, 0, receivedBuffer.length, Charsets.UTF_8)

            println("Received: $receivedMessage")

            if(receivedMessage == "exit"){
                socket.close()
                break
            }
            // Construct response to send back to sender
            print("Enter response: ")
            val response = Scanner(System.`in`).nextLine()

            // convert string to byte array
            val responseData = response.toByteArray(Charsets.UTF_8)

            // create packet with response data and received buffer address and port
            val responsePacket = DatagramPacket(responseData, responseData.size, receivedBuffer.address, receivedBuffer.port)

            // send packet
            socket.send(responsePacket)

            if(response == "exit"){
                socket.close()
                break
            }
        }
    }
}