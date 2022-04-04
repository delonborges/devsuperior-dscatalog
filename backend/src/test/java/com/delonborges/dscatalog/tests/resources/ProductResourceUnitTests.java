package com.delonborges.dscatalog.tests.resources;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.factory.ProductDTOFactory;
import com.delonborges.dscatalog.services.ProductService;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import com.delonborges.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    @MockBean
    private ProductService productService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private String jsonBody;
    private String username;
    private String password;

    @BeforeEach
    protected void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        ProductDTO productDTO = ProductDTOFactory.createProductDTOWithCategory();
        PageImpl<ProductDTO> page = new PageImpl<>(List.of(productDTO));
        jsonBody = objectMapper.writeValueAsString(productDTO);
        username = "maria@gmail.com";
        password = "123456";

        when(productService.findAllPaged(any(), any(), any())).thenReturn(page);

        when(productService.findById(existingId)).thenReturn(productDTO);
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(productService.insert(any())).thenReturn(productDTO);

        when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService)
                   .delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService)
                                                .delete(nonExistingId);
        doThrow(DatabaseIntegrityViolationException.class).when(productService)
                                                          .delete(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDTO() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(post("/products").header("Authorization", "Bearer " + accessToken)
                                         .content(jsonBody)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(put("/products/{id}", existingId).header("Authorization", "Bearer " + accessToken)
                                                         .content(jsonBody)
                                                         .contentType(MediaType.APPLICATION_JSON)
                                                         .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(put("/products/{id}", nonExistingId).header("Authorization", "Bearer " + accessToken)
                                                            .content(jsonBody)
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(delete("/products/{id}", existingId).header("Authorization", "Bearer " + accessToken)
                                                            .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(delete("/products/{id}", nonExistingId).header("Authorization", "Bearer " + accessToken)
                                                               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        mockMvc.perform(delete("/products/{id}", dependentId).header("Authorization", "Bearer " + accessToken)
                                                             .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }
}
