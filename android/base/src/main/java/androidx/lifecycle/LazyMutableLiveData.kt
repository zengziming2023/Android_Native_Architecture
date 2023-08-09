package androidx.lifecycle

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class LazyMutableLiveData<T> : MutableLiveData<T> {
    private var startVersion by Delegates.notNull<Int>()

    constructor() : super() {
        startVersion = version
    }

    constructor(default: T) : super(default) {
        startVersion = version
    }

    /**
     * call on LIFECYCLE START and not stick
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, LazyObserver(observer))
    }

    fun observeStick(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(observer)
    }


    private inner class LazyObserver<T>(val observer: Observer<in T>) : Observer<T> {
        private var shouldIgnore = AtomicBoolean(
            this@LazyMutableLiveData.version > this@LazyMutableLiveData.startVersion
        )

        override fun onChanged(value: T) {
            if (shouldIgnore.compareAndSet(true, false)) {
                return
            }
            observer.onChanged(value)
        }
    }

}