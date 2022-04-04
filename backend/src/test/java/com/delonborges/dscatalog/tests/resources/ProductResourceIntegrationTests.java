package com.delonborges.dscatalog.tests.resources;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.factory.ProductDTOFactory;
import com.delonborges.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc @Transactional public class ProductResourceIntegrationTests {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private TokenUtil tokenUtil;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;
    private ProductDTO productDTO;
    private String jsonBody;
    private String username;
    private String password;

    @BeforeEach protected void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 26L;
        totalProducts = 25L;
        productDTO = ProductDTOFactory.createProductDTOWithCategory();
        jsonBody = objectMapper.writeValueAsString(productDTO);
        username = "maria@gmail.com";
        password = "123456";
    }

    @Test public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        String firstItem = "MacBook Pro";
        String lastItem = "PC Gamer Hera";

        mockMvc.perform(get("/products?page=0&size=10&sort=name,asc").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalElements").value(totalProducts))
               .andExpect(jsonPath("$.content").exists())
               .andExpect(jsonPath("$.content[0].name").value(firstItem))
               .andExpect(jsonPath("$.content[9].name").value(lastItem));
    }

    @Test public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        String productName = productDTO.getName();
        Double productPrice = productDTO.getPrice();

        mockMvc.perform(put("/products/{id}", existingId).header("Authorization", "Bearer " + accessToken)
                                                         .content(jsonBody)
                                                         .contentType(MediaType.APPLICATION_JSON)
                                                         .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(existingId))
               .andExpect(jsonPath("$.name").value(productName))
               .andExpect(jsonPath("$.price").value(productPrice));
    }

    @Test public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(put("/products/{id}", nonExistingId).header("Authorization", "Bearer " + accessToken)
                                                            .content(jsonBody)
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}
