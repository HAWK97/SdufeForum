package com.hawk.data.vo;

import com.hawk.data.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserList {

    private List<UserDTO> userDTOList;

    private Integer totalCount;
}
