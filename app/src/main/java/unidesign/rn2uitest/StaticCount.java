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
    TextView select_toolbar_title;

    StaticCount(TextView select_toolbar_title) {
        this.count = 0;
        this.select_toolbar_title = select_toolbar_title;
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (count != -100)
            changeObservable.onNext(count);
        else
            changeObservable.onComplete();
    }

    public void setCountComplete() {
            changeObservable.onComplete();
    }

    public Observable<Integer> getCountChanges() {
        return changeObservable;
    }

    public StaticCount CustomonNext(TextView select_toolbar_title) {
        return this;
    }


}
