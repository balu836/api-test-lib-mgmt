package uk.gov.dwp.utils.casehistory;

import java.time.LocalDateTime;

/**
 * Holds the details of a single case history entry.
 * Currently holds the entry date, and the rest of the data as separate fields - their validation would be treated differently.
 */
public class CaseHistoryRecord {
    private LocalDateTime date;
    private String entry;

    public CaseHistoryRecord(LocalDateTime date, String entry) {
        this.date = date;
        this.entry = entry;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public String getEntry() {
        return this.entry;
    }

    @Override
    public String toString() {
        return this.date + " : " + this.entry;
    }
}
