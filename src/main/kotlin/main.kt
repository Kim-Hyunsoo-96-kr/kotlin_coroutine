import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main(){
    println("before")

    println("0. Thread.currentThread().name : ${Thread.currentThread().name}")
//    suspendCoroutine<Unit> { continuation ->
//        executor.schedule({continuation.resume(Unit)}, 2000, TimeUnit.MILLISECONDS)
//    }
    delay(2000)

    println("1. Thread.currentThread().name : ${Thread.currentThread().name}")
    println("after")
}

private val executor = Executors.newSingleThreadScheduledExecutor{
    Thread(it, "scheduler").also{ it.isDaemon = true }.also{
        println("it.name : ${it.name}")
        println("2. Thread.currentThread().name : ${Thread.currentThread().name}")
    }
}

suspend fun delay(timeMillis:Long):Unit = suspendCoroutine { cont ->
    executor.schedule({cont.resume(Unit)}, timeMillis, TimeUnit.MILLISECONDS)
}
fun continueAfterSecond(continuation: Continuation<Unit>){
    thread {
        Thread.sleep(1000)
        continuation.resume(Unit)
    }
}