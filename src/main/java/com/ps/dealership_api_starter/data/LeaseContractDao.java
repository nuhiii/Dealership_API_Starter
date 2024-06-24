package com.ps.dealership_api_starter.data;

import com.ps.dealership_api_starter.models.LeaseContract;

public interface LeaseContractDao {
    LeaseContract getById(int contractId);
    LeaseContract create(LeaseContract leaseContract);
}

