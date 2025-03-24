package pawz.Components.Internals;

public interface Observer<ObservableData> {

    void getUpdate(ObservableData data);
}
