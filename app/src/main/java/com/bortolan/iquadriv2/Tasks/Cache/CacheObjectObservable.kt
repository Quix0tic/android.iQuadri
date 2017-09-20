package com.bortolan.iquadriv2.Tasks.Cache

import android.util.Log
import io.reactivex.Observable
import java.io.*

class CacheObjectObservable(private val file: File) {
    private val TAG = CacheObjectObservable::class.java.simpleName

    fun <T> getCachedObject(klazz: Class<T>): Observable<T> {
        return Observable.fromCallable {
            var objectInputStream: ObjectInputStream? = null
            var obj: Any? = null
            try {
                val fileInputStream = FileInputStream(file)
                objectInputStream = ObjectInputStream(fileInputStream)
                obj = objectInputStream.readObject()
            } catch (e: FileNotFoundException) {
                Log.w(TAG, "Cache not found.")
            } catch (e: EOFException) {
                Log.e(TAG, "Error while reading cache! (EOF) ")
            } catch (e: StreamCorruptedException) {
                Log.e(TAG, "Corrupted cache!")
            } catch (e: IOException) {
                Log.e(TAG, "Error while reading cache!")
            } catch (e: ClassCastException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            klazz.cast(obj)
        }
    }
}
