package cc.gavin.grumman.zeta.service;

import cc.gavin.grumman.zeta.util.Constants;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by user on 12/29/16.
 */
public class CollectionConstatService {

    public static void collection(){
        initStorage();
        initStoreStorage();
        initPPPOutgoing();
        initPPPStorage();
        initDepartmentAllocation();
        initDistributionAllocation();
    }

    private  static void initStorage(){
        List<Record> materiel_types =  Db.find("select distinct materiel_type from storage_info");
        List<Record> suppliers =  Db.find("select distinct supplier from storage_info");
        List<Record> income_departments =  Db.find("select distinct income_department from storage_info");
        List<Record> expenditure_departments =  Db.find("select distinct expenditure_department from storage_info");
        Constants.storage.materiel_type.clear();
        Constants.storage.supplier.clear();
        Constants.storage.income_department.clear();
        Constants.storage.expenditure_department.clear();
        for(Record record:materiel_types){
            Constants.storage.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:suppliers){
            Constants.storage.supplier.put(record.getStr("supplier"),record.getStr("supplier"));
        }
        for(Record record:income_departments){
            Constants.storage.income_department.put(record.getStr("income_department"),record.getStr("income_department"));
        }
        for(Record record:expenditure_departments){
            Constants.storage.expenditure_department.put(record.getStr("expenditure_department"),record.getStr("expenditure_department"));
        }
    }

    private static void initStoreStorage(){
        List<Record> storage_materiel_types =  Db.find("select distinct materiel_type from store_storage_info");
        List<Record> storage_suppliers =  Db.find("select distinct supplier from store_storage_info");
        List<Record> storage_income_departments =  Db.find("select distinct income_department from store_storage_info");
        List<Record> storage_expenditure_departments =  Db.find("select distinct expenditure_department from store_storage_info");
        Constants.storestorage.materiel_type.clear();
        Constants.storestorage.supplier.clear();
        Constants.storestorage.income_department.clear();
        Constants.storestorage.expenditure_department.clear();
        for(Record record:storage_materiel_types){
            Constants.storestorage.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:storage_suppliers){
            Constants.storestorage.supplier.put(record.getStr("supplier"),record.getStr("supplier"));
        }
        for(Record record:storage_income_departments){
            Constants.storestorage.income_department.put(record.getStr("income_department"),record.getStr("income_department"));
        }
        for(Record record:storage_expenditure_departments){
            Constants.storestorage.expenditure_department.put(record.getStr("expenditure_department"),record.getStr("expenditure_department"));
        }
    }

    private static void initPPPOutgoing(){
        List<Record> storage_materiel_types =  Db.find("select distinct materiel_type from ppp_outgoing_info");
        List<Record> storage_suppliers =  Db.find("select distinct supplier from ppp_outgoing_info");
        List<Record> storage_income_departments =  Db.find("select distinct income_department from ppp_outgoing_info");
        List<Record> storage_expenditure_departments =  Db.find("select distinct expenditure_department from ppp_outgoing_info");
        Constants.pppoutgoing.materiel_type.clear();
        Constants.pppoutgoing.supplier.clear();
        Constants.pppoutgoing.income_department.clear();
        Constants.pppoutgoing.expenditure_department.clear();
        for(Record record:storage_materiel_types){
            Constants.pppoutgoing.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:storage_suppliers){
            Constants.pppoutgoing.supplier.put(record.getStr("supplier"),record.getStr("supplier"));
        }
        for(Record record:storage_income_departments){
            Constants.pppoutgoing.income_department.put(record.getStr("income_department"),record.getStr("income_department"));
        }
        for(Record record:storage_expenditure_departments){
            Constants.pppoutgoing.expenditure_department.put(record.getStr("expenditure_department"),record.getStr("expenditure_department"));
        }
    }

    private static void initPPPStorage(){
        List<Record> storage_materiel_types =  Db.find("select distinct materiel_type from ppp_storage_info");
        List<Record> storage_suppliers =  Db.find("select distinct supplier from ppp_storage_info");
        List<Record> storage_income_departments =  Db.find("select distinct income_department from ppp_storage_info");
        List<Record> storage_expenditure_departments =  Db.find("select distinct expenditure_department from ppp_storage_info");
        Constants.pppstorage.materiel_type.clear();
        Constants.pppstorage.supplier.clear();
        Constants.pppstorage.income_department.clear();
        Constants.pppstorage.expenditure_department.clear();
        for(Record record:storage_materiel_types){
            Constants.pppstorage.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:storage_suppliers){
            Constants.pppstorage.supplier.put(record.getStr("supplier"),record.getStr("supplier"));
        }
        for(Record record:storage_income_departments){
            Constants.pppstorage.income_department.put(record.getStr("income_department"),record.getStr("income_department"));
        }
        for(Record record:storage_expenditure_departments){
            Constants.pppstorage.expenditure_department.put(record.getStr("expenditure_department"),record.getStr("expenditure_department"));
        }
    }

    private static void initDepartmentAllocation(){
        List<Record> storage_materiel_types =  Db.find("select distinct materiel_type from department_allocation_info");
        List<Record> storage_suppliers =  Db.find("select distinct supplier from department_allocation_info");
        List<Record> storage_income_departments =  Db.find("select distinct income_department from department_allocation_info");
        List<Record> storage_expenditure_departments =  Db.find("select distinct expenditure_department from department_allocation_info");
        Constants.departmentallocation.materiel_type.clear();
        Constants.departmentallocation.supplier.clear();
        Constants.departmentallocation.income_department.clear();
        Constants.departmentallocation.expenditure_department.clear();
        for(Record record:storage_materiel_types){
            Constants.departmentallocation.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:storage_suppliers){
            Constants.departmentallocation.supplier.put(record.getStr("supplier"),record.getStr("supplier"));
        }
        for(Record record:storage_income_departments){
            Constants.departmentallocation.income_department.put(record.getStr("income_department"),record.getStr("income_department"));
        }
        for(Record record:storage_expenditure_departments){
            Constants.departmentallocation.expenditure_department.put(record.getStr("expenditure_department"),record.getStr("expenditure_department"));
        }
    }

    private static void initDistributionAllocation(){
        List<Record> storage_materiel_types =  Db.find("select distinct materiel_type from distribution_allocation_info");
        List<Record> distribution_departments =  Db.find("select distinct distribution_department from distribution_allocation_info");
        List<Record> distribution_allocations =  Db.find("select distinct outgoing_department from distribution_allocation_info");
        Constants.distributionallocation.materiel_type.clear();
        Constants.distributionallocation.outgoing_department.clear();
        Constants.distributionallocation.distribution_department.clear();
        for(Record record:storage_materiel_types){
            Constants.distributionallocation.materiel_type.put(record.getStr("materiel_type"),record.getStr("materiel_type"));
        }
        for(Record record:distribution_departments){
            Constants.distributionallocation.distribution_department.put(record.getStr("distribution_department"),record.getStr("distribution_department"));
        }
        for(Record record:distribution_allocations){
            Constants.distributionallocation.outgoing_department.put(record.getStr("outgoing_department"),record.getStr("outgoing_department"));
        }

    }
}
