package org.example.moneymachine.model.DTO;

import org.example.moneymachine.model.APIDTOInterface;
import org.example.moneymachine.model.Adapter;
import org.example.moneymachine.model.entity.*;

public interface DTOAdapter extends Adapter<BankApiEntity,APIDTOInterface> {

    @Override
    APIDTOInterface from(BankApiEntity dependentClass);

    @Override
    BankApiEntity to(APIDTOInterface targetClass);
}
