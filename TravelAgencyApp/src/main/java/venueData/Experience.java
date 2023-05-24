package venueData;

public class Experience extends Venue {
    public Experience(Universal.Address address, Long id, String name, String type) {
        super(address, id, name, type);
    }

    private String activityType;
    private Integer groupSizeLimit;

    public void setActivityType(String activityType){ this.activityType = activityType; }

    public String getActivityType(){ return activityType; }

    public void setGroupSizeLimit(Integer groupSizeLimit){ this.groupSizeLimit = groupSizeLimit; }

    public Integer getGroupSizeLimit(){ return groupSizeLimit; }
}
