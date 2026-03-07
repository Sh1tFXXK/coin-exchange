package com.wc.mappers;

import com.wc.domain.TradeArea;
import dto.TradeAreaDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TradeAreaDtoMappers {

    TradeAreaDtoMappers INSTANCE = Mappers.getMapper(TradeAreaDtoMappers.class) ;

    TradeArea toConvertEntity(TradeAreaDto source) ;


    TradeAreaDto toConvertDto(TradeArea source) ;


    List<TradeArea> toConvertEntity(List<TradeAreaDto> source) ;


   List<TradeAreaDto> toConvertDto(List<TradeArea> source) ;
}
