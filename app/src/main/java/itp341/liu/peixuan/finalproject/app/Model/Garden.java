package itp341.liu.peixuan.finalproject.app.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Peixuan on 12/3/17.
 */

public class Garden {
    private String name;
    private String location;
    private String leader;
    private ArrayList<String> members;
    private String status;
    private String statusDetail;
    private String imageUrl;
    private String description;
    private String statusLastUpdatedBy;
    private ArrayList<String> authUsers;

    public Garden(){}

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<String> getAuthUsers() {
        return authUsers;
    }

    public void setAuthUsers(ArrayList<String> authUsers) {
        this.authUsers = authUsers;
    }

    public String getStatusLastUpdatedBy() {
        return statusLastUpdatedBy;
    }

    public void setStatusLastUpdatedBy(String statusLastUpdatedBy) {
        this.statusLastUpdatedBy = statusLastUpdatedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
