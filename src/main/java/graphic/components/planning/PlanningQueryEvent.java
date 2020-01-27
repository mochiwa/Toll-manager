package graphic.components.planning;

import graphic.components.event.QueryEvent;
import javafx.event.EventType;
import tollmanager.application.query.WeekPlanningQuery;

public class PlanningQueryEvent {
    public static final EventType<QueryWeekPlanningEvent> WEEK_PLANNING = new EventType<>("QUERY_WEEK_PLANNING");

    public static QueryEvent of(WeekPlanningQuery query){ return new QueryWeekPlanningEvent(query); }



      public static class QueryWeekPlanningEvent extends QueryEvent<WeekPlanningQuery> {
        private QueryWeekPlanningEvent(WeekPlanningQuery query) {
            super(WEEK_PLANNING);
            setQuery(query);
        }
    }
}
