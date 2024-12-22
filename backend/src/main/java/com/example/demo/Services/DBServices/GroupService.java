package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Group;
import com.example.demo.Repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Exceptions.Models.GroupNotFoundException;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group getGroupByEventId(Long id){
        return groupRepository.findByEventId(id)
                .orElseThrow(()-> new GroupNotFoundException("Group not found associated with this event ID."));
    }
}
