# 3장. 중단은 어떻게 작동할까?

* 코루틴은 중단되었을 때 Continuation 객체를 반환
* Continuation을 이용하면 멈췄던 곳에서 다시 코루틴을 실행할 수 있다.
* 코루틴과 스레드의 차이점은 코루틴은 멈춤과 저장이 가능하지만 스레드는 멈춤만 가능
* 중단했을 때, 코루틴은 어떠한 자원도 사용하지 않는다.
* 코루틴은 다른 스레드에서 실행가능
* Continuation 객체는 직렬화, 역직렬화가 가능하며 다시 실행될 수 있다.

### 재개
* 작업을 재개하려면 코루틴이 필요
* 코루틴은 runBlocking, launch와 같은 빌더를 통해 만들 수 있다.
* 중단 함수 : 코루틴을 중단할 수 있는 함수
* 중단 함수는 반드시 다른 중단 함수(코루틴)에 의해 호출되어야 한다.
* 중단 함수는 중단할 수 있는 곳이 필요하다.
```kotlin
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
```