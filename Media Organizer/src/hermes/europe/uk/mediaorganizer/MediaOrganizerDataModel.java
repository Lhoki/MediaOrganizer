package hermes.europe.uk.mediaorganizer;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MediaOrganizerDataModel {

    StringProperty name = new SimpleStringProperty();
    StringProperty type = new SimpleStringProperty();
    StringProperty category = new SimpleStringProperty();
    StringProperty comment = new SimpleStringProperty();
    ObservableList categoryList = FXCollections.observableArrayList();

    private final ObjectProperty<Integer> id = new SimpleObjectProperty<>(null);

    public MediaOrganizerDataModel(){
    }

    public MediaOrganizerDataModel(Integer id, String name, String type, String category, String comment){
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
        this.category.set(category);
        this.comment.set(comment);
    }

    public MediaOrganizerDataModel(String name, String type){
        this.name.set(name);
        this.type.set(type);
    }

    public MediaOrganizerDataModel(Integer id, String name, String type){
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
    }

    public MediaOrganizerDataModel(Integer id, String name, String type, String category){
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
        this.category.set(category);
    }

    public MediaOrganizerDataModel(Integer id, String name, String type, ObservableList<String> categoryList){
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
        this.setCategoryList(categoryList);
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public StringProperty getTypeProperty() {
        return type;
    }

    public StringProperty getCategoryProperty() {
        return category;
    }


    //Boilerplate

    public Integer getId() {
        return id.get();
    }

    public ObjectProperty<Integer> idProperty() {
        return id;
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public String getComment() {
        return comment.get();
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getCategory() {
        return category.get();
    }

    public ObservableList<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ObservableList<String> categoryList) {
        this.categoryList = categoryList;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }


}
