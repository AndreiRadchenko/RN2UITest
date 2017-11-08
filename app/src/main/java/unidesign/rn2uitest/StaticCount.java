package unidesign.rn2uitest;

/**
 * Created by United on 11/8/2017.
 */

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by United on 11/8/2017.
 */

public class StaticCount {
    private PublishSubject<StaticCount> changeObservable = PublishSubject.create();
    private int count;

    StaticCount() {
        this.count = 0;
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
            changeObservable.onNext(this);
    }

    public void setCountComplete() {
            changeObservable.onComplete();
    }

    public Observable<StaticCount> getCountChanges() {
        return changeObservable;
    }

}
