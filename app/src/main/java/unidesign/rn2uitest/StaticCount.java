package unidesign.rn2uitest;

/**
 * Created by United on 11/8/2017.
 */

import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by United on 11/8/2017.
 */

public class StaticCount {
    private PublishSubject<Integer> changeObservable = PublishSubject.create();

    private int count;

    StaticCount() {
        this.count = 0;
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        changeObservable.onNext(count);
    }

    public void setCountComplete() {
            changeObservable.onComplete();
    }

    public Observable<Integer> getCountChanges() {
        //if (changeObservable.hasComplete())
        return changeObservable;
    }

}
