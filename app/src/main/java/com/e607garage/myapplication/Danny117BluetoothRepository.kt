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
    //private val myAddress = "98:D3:31:90:1E:1D"  // I know but I have to get the proof of concept done
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private val appC: Context = context
    private var danny117BTSocket: BluetoothSocket? = null
    val rev = ByteArray(256)

    //receive from bluetooth then update then database
    //the updated value in database will flow where it needs to go :)
    fun fetchLatestReply() {
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = ByteArray(18)
            var offset = 0
            var length = 18
            val bx = getMyInputStream()
            if (bx != null) {
                var cx = 0
                while (true) {
                    //normal multi read
                    if (cx in 1..17) {
                        length = 18 - cx
                        offset = cx
                    }
                    try {
                        cx = bx.read(byteArray, offset, length)
                    } catch (e: java.lang.Exception) {
                        Log.e("DANNY117", e.toString())
                        Log.e("DANNY117", offset.toString())
                        Log.e("DANNY117", length.toString())
                        Log.e("DANNY117", cx.toString())
                    }
                    //get out of here
                    if (cx == -1) {
                        break
                    }
                    cx += offset
                    //simple is it my data or garbage
                    // 18 bytes
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
                    // byte 12 mode
                    // byte 13 adj1
                    // byte 14 adj2
                    // byte 15 marker 232
                    // byte 16 marker 34
                    // byte 17 marker 182
                    if (cx == 18
                        && byteArray[0].toUByte() == 232.toUByte()
                        && byteArray[1].toUByte() == 34.toUByte()
                        && byteArray[2].toUByte() == 182.toUByte()
                        && byteArray[3].toUByte() == 0.toUByte()
                        && byteArray[4].toUByte() == 0.toUByte()
                        && byteArray[5].toUByte() <= 32.toUByte()
                        && byteArray[5].toUByte() >= 0.toUByte()
                        && byteArray[6].toUByte() <= 32.toUByte()
                        && byteArray[6].toUByte() >= 0.toUByte()
                        && byteArray[15].toUByte() == 232.toUByte()
                        && byteArray[16].toUByte() == 34.toUByte()
                        && byteArray[17].toUByte() == 182.toUByte()
                    ) {
                        cx = 0
                        offset = 0
                        length = 18
                        val i = byteArray[6].toInt()
                        val w: Word = wordViewModel.getWord(i)
                        if (w != null) {
                            w.rechecked = (byteArray[7].toInt() != 0)
                            w.recolor = Color.argb(
                                rev[byteArray[8].toUByte().toInt()].toUByte().toInt(),
                                byteArray[9].toUByte().toInt(),
                                byteArray[10].toUByte().toInt(),
                                byteArray[11].toUByte().toInt()
                            )
                            w.remode = byteArray[12].toUByte().toInt()
                            w.readj1 = byteArray[13].toUByte().toInt()
                            w.readj2 = byteArray[14].toUByte().toInt()
                            //finally update the word
                            wordViewModel.update(w)
                        }
                    }
                    if (cx == 17) {
                        //this has only fired when I forced it fire??? pretty stable
                        //buffer out of alignment 18 bytes but they
                        //didn't get past the check
                        //something simple
                        //move the array up one position so one more byte can be read
                        //eventually it will find its way back
                        // move the array back 1 position and read another byte on
                        //next loop
                        var n = false
                        for (j: Int in 0..14) {
                            byteArray[j] = byteArray[j + 1]
                            if (
                                byteArray[j].toUByte() != 0.toUByte()
                            ) {
                                n = true
                            }
                        }
                        if (n) {
                            cx = 17   //this only fired when I forced it fire
                        } else {
                            cx = 18
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
            var i = 255
            for (k in 0..255){
                rev[k] = i.toByte()
                i--
            }
            if (checkSelfPermission(
                    appC, android.Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val ada = bluetoothManager.adapter
                ada.bondedDevices.forEach {
                    if (it.name=="Danny117HDWS2812B") {
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

    fun writeB(word: Word) {
        if (danny117BTSocket != null) {
            CoroutineScope(Dispatchers.IO).launch {

                val b = "writeB:" + Thread.currentThread().name
                Log.d("DANNY117", b)
                val outS = ByteArray(18)
                // 18 bytes
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
                // byte 11 color blue
                // byte 12 mode
                // byte 13 adj1
                // byte 14 adj2
                // byte 15 marker 232
                // byte 16 marker 34
                // byte 17 marker 182
                outS[0] = 232.toByte()
                outS[1] = 34.toByte()
                outS[2] = 182.toByte()
                outS[3] = 0.toByte()
                outS[4] = 0.toByte()
                outS[5] = 0.toByte()
                //actually only need 1 byte here but
                //the three zeros are nice as markers
                //from id range is 1 to 32 so I don't need
                //to actually send the whole id
                outS[6] = word._id.toByte()
                if (word.checked) {
                    outS[7] = 255.toByte()
                } else {
                    outS[7] = 0.toByte()
                }
                outS[8] = rev[Color.alpha(word.color)]
                outS[9] = Color.red(word.color).toByte()
                outS[10] = Color.green(word.color).toByte()
                outS[11] = Color.blue(word.color).toByte()
                outS[12] = word.mode.toByte()
                outS[13] = word.adj1.toByte()
                outS[14] = word.adj2.toByte()
                outS[15] = 232.toByte()
                outS[16] = 34.toByte()
                outS[17] = 182.toByte()
                val os = getMyOutputStream()
                if (os != null) {
                    os.write(outS)
                }
            }
        }
    }
}



