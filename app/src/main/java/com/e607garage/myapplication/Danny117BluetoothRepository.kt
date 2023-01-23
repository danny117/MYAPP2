package com.e607garage.myapplication

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class Danny117BluetoothRepository(
    private val context: Context, private val wordViewModel: WordViewModel
) {
    val myaddress = "98:D3:31:90:1E:1D"  // I know but I have to get the proof of concept done
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private val appC: Context = context
    private var danny117BTSocket: BluetoothSocket? = null
    private val newline = 10.toByte()


    private fun read4BytesFromBuffer(buffer: ByteArray, offset: Int): Int {
        return (buffer[offset].toInt() shl 24) or
                (buffer[offset + 1].toInt() and 0xff shl 16) or
                (buffer[offset + 2].toInt() and 0xff shl 8) or
                (buffer[offset + 3].toInt() and 0xff)
    }

    //receive from bluetooth then update then database
    //the updated value in database will flow where it needs to go :)
    fun fetchLatestReply() {
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = ByteArray(16)
            val bx = getMyInputStream()
            if (bx != null) {
                var cx = 0
                while (cx != -1) {
                    cx = bx.read(byteArray)
                    //simple is it my data or garbage
                    // 16 bytes
                    // byte 0 marker 232
                    // byte 1 marker 34
                    // byte 2 marker 182
                    // byte 3 id
                    // byte 4 id
                    // byte 5 id
                    // byte 6 id
                    // byte 7 on/off
                    // byte 8 color alpha
                    // byte 9 color red
                    // byte 10 color green
                    // byte 11 color blue
                    // byte 12 modes
                    // byte 13 marker 232
                    // byte 14 marker 34
                    // byte 15 marker 182
                    if (cx == 16
                        && byteArray[0].toInt() == 232
                        && byteArray[1].toInt() == 34
                        && byteArray[2].toInt() == 182
                        && byteArray[13].toInt() == 232
                        && byteArray[14].toInt() == 34
                        && byteArray[15].toInt() == 182
                    ) {
                        val i = read4BytesFromBuffer(byteArray, 3)
                        val w: Word = wordViewModel.getWord(i)
                        if (w._id != 0) {
                            w.rechecked = (byteArray[5].toInt() == 0)
                            w.color = Color.argb(
                                byteArray[6].toInt(),
                                byteArray[7].toInt(),
                                byteArray[8].toInt(),
                                byteArray[9].toInt()
                            )
                            //finally update the word
                            wordViewModel.update(w)
                        }
                    }
                }
            }
        }
    }

    private fun getMyInputStream(): InputStream? {
        var a: InputStream? = null
        val b = getMySocket()
        if (b != null) {
            a = b.inputStream
        }
        return a
    }

    private fun getMyOutputStream(): OutputStream? {
        var a: OutputStream? = null
        val b = getMySocket()
        if (b != null) {
            a = b.outputStream
        }
        return a
    }

    private fun getMySocket(): BluetoothSocket? {
        val bluetoothManager: BluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (danny117BTSocket == null) {
            if (checkSelfPermission(
                    appC, android.Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val ada = bluetoothManager.adapter
                ada.bondedDevices.forEach {
                    if (it.address == myaddress) {
                        danny117BTSocket = it.createRfcommSocketToServiceRecord(MY_UUID)
                    }
                }
            }
        }
        if (danny117BTSocket != null) {
            if (danny117BTSocket?.isConnected == false) {
                try {
                    danny117BTSocket?.connect()
                } catch (e: java.lang.Exception) {
                    Log.e("DANNY117", e.toString())
                    danny117BTSocket?.close()
                    danny117BTSocket = null
                } finally {

                }
            }
        }
        if (danny117BTSocket != null) {
            if (danny117BTSocket?.isConnected != true) {
                danny117BTSocket?.close()
                danny117BTSocket = null
            }
        }

        return danny117BTSocket
    }

    internal fun UInt.to4ByteArrayInBigEndian(): ByteArray =
        (3 downTo 0).map {
            (this shr (it * Byte.SIZE_BITS)).toByte()
        }.toByteArray()


    fun writeB(word: Word) {
        if (danny117BTSocket != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val b = "writeB:" + Thread.currentThread().name
                Log.d("DANNY117", b)
                val outS = ByteArray(17)
                // 16 bytes
                // byte 0 marker 232
                // byte 1 marker 34
                // byte 2 marker 182
                // byte 3 id value
                // byte 4 id value
                // byte 5 id value
                // byte 6 id value
                // byte 7 on/off
                // byte 8 color alpha
                // byte 9 color red
                // byte 10 color green
                // byte 12 color blue
                // byte 12 modes
                // byte 13 marker 232
                // byte 14 marker 34
                // byte 15 marker 182
                outS[0] = 232.toByte()
                outS[1] = 34.toByte()
                outS[2] = 182.toByte()
                //actually only need 2 bytes here
                val ba = word._id.toUInt().to4ByteArrayInBigEndian()
                outS[3] = ba[0]
                outS[4] = ba[1]
                outS[5] = ba[2]
                outS[6] = ba[3]
                if (word.checked) {
                    outS[7] = 255.toByte()
                } else {
                    outS[7] = 0.toByte()
                }
                outS[8] = Color.alpha(word.color).toByte()
                outS[9] = Color.red(word.color).toByte()
                outS[10] = Color.green(word.color).toByte()
                outS[11] = Color.blue(word.color).toByte()
                outS[12] = 0.toByte()
                outS[13] = 232.toByte()
                outS[14] = 34.toByte()
                outS[15] = 182.toByte()
                outS[16] = newline
                val os = getMyOutputStream()
                if (os != null) {
                    os.write(outS)
                }
            }
        }
    }
}



