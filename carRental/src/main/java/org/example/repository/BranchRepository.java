package org.example.repository;

import org.example.models.Branch;

import java.util.HashMap;
import java.util.Map;

public class BranchRepository {
    Map<String, Branch> branchMap = new HashMap<>();

    public void addBranch(Branch branch) {
        branchMap.put(branch.getId(), branch);
    }

    public Branch getBranchById(String branchId) {
        return branchMap.get(branchId);
    }

}
