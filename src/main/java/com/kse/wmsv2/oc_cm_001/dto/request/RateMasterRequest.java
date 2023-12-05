package com.kse.wmsv2.oc_cm_001.dto.request;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.RateMasterDto;

@Data
public class RateMasterRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    /** 開始日 */
    private String startDate;

    private List<RateMasterDto> listRateMasterDto;

    /**
     * Set page for get data.
     * 
     * @param adminSearch AdminSearch
     * @return page number
     */
    public static RateMasterRequest setPaging(RateMasterRequest rateMasterRequest) {
        if (rateMasterRequest.getPage() != null && rateMasterRequest.getItemPerPage() != null) {
            rateMasterRequest.setPage((rateMasterRequest.getPage() - 1) * rateMasterRequest.getItemPerPage());
        }
        return rateMasterRequest;
    }
}