package com.northstar.crm.event;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ProcessedEventStore {

  private final Set<String> seen = ConcurrentHashMap.newKeySet();

  /** @return true if this is the first time seeing eventId */
  public boolean markIfNew(String eventId) {
    // Set.add returns true only when the id was NOT already present -> first sighting.
    // Lab: in-memory; production must use a durable, shared store (DB unique key).
    return seen.add(eventId);
  }
}
