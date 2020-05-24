/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FindUserResponse {

    private final String id;
    private final String name;

}
