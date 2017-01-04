package cc.gavin.grumman.zeta.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 12/29/16.
 */
public interface Constants {

    interface storage{
        /**
         *物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         *供应商
         */
        Map<String,String> supplier =new HashMap<>();

        /**
         *收料部门
         */
        Map<String,String> income_department =new HashMap<>();

        /**
         *发料部门
         */
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface storestorage{
        /**
         *物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         *供应商
         */
        Map<String,String> supplier =new HashMap<>();

        /**
         *收料部门
         */
        Map<String,String> income_department =new HashMap<>();

        /**
         *发料部门
         */
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface pppoutgoing{
        /**
         *物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         *供应商
         */
        Map<String,String> supplier =new HashMap<>();

        /**
         *收料部门
         */
        Map<String,String> income_department =new HashMap<>();

        /**
         *发料部门
         */
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface pppstorage{
        /**
         *物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         *供应商
         */
        Map<String,String> supplier =new HashMap<>();

        /**
         *收料部门
         */
        Map<String,String> income_department =new HashMap<>();

        /**
         *发料部门
         */
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface departmentallocation{
        /**
         *物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         *供应商
         */
        Map<String,String> supplier =new HashMap<>();

        /**
         *收料部门
         */
        Map<String,String> income_department =new HashMap<>();

        /**
         *发料部门
         */
        Map<String,String> expenditure_department =new HashMap<>();
    }

    interface distributionallocation{
        /**
         * 物料类别
         */
        Map<String,String> materiel_type =new HashMap<>();

        /**
         * 配送部门
         */
        Map<String,String> distribution_department =new HashMap<>();
        Map<String,String> outgoing_department =new HashMap<>();
    }




}
