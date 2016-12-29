package cc.gavin.grumman.zeta.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 12/29/16.
 */
public interface Constants {

    interface storage{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> supplier =new HashMap<>();
        Map<String,String> income_department =new HashMap<>();
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface storestorage{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> supplier =new HashMap<>();
        Map<String,String> income_department =new HashMap<>();
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface pppoutgoing{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> supplier =new HashMap<>();
        Map<String,String> income_department =new HashMap<>();
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface pppstorage{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> supplier =new HashMap<>();
        Map<String,String> income_department =new HashMap<>();
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface departmentallocation{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> supplier =new HashMap<>();
        Map<String,String> income_department =new HashMap<>();
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface distributionallocation{
        Map<String,String> materiel_type =new HashMap<>();
        Map<String,String> distribution_department =new HashMap<>();
        Map<String,String> distribution_allocation =new HashMap<>();
    }




}
