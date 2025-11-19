package bz.sample

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Scanner

fun main() {

    val ip = "127.0.0.1"
    val port = 11323

    val inetAddress = try {
        InetAddress.getByName(ip)
    } catch (e: Exception) {
        error("Invalid IP address: $ip")
    }

    // Construct DatagramSocket w/o port bcus we are the client
    val datagramSocket = DatagramSocket()

    val receiveData = ByteArray(1024)

    val receivedPacket = DatagramPacket(receiveData, receiveData.size)

    while (true) {
        print("Enter message: ")
        val message = Scanner(System.`in`).nextLine()

        val messageBytes = message.toByteArray()

        val datagramPacket = try {
            DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
        } catch (e: Exception) {
            e.printStackTrace()
            error("Invalid packet construction")
        }

        try {
            datagramSocket.send(datagramPacket)
        } catch (e: Exception) {
            e.printStackTrace()
            error("Error sending datagram packet")
        }

        if(message == "exit"){
            datagramSocket.close()
            break
        }

        datagramSocket.receive(receivedPacket)

        val receivedMessage = String(receivedPacket.data, 0, receivedPacket.length)

        println("Received: $receivedMessage")

        if(receivedMessage == "exit"){
            datagramSocket.close()
            break
        }
    }
}
