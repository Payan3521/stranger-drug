package com.microserviceone.users.registrationApi.infraestructure.persistance.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.mapper.UserMapper;
import com.microserviceone.users.registrationApi.infraestructure.persistance.repository.ORMregister;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterRegister implements IRegisterRepository{

    private final ORMregister ormRegister;
    private final UserMapper userMapper;
    private final LoggingService loggingService;

    @Override
    public User save(User user) {
        try {
            loggingService.logDebug("AdapterRegister: Guardando usuario en base de datos - Email: {}, Tipo: {}", 
                user.getEmail(), user.getClass().getSimpleName());
            
            UserEntity userEntity = userMapper.toEntity(user);
            loggingService.logDebug("AdapterRegister: Usuario convertido a entidad - Email: {}", user.getEmail());
            
            UserEntity savedUserEntity = ormRegister.save(userEntity);
            loggingService.logDebug("AdapterRegister: Usuario guardado en base de datos - ID: {}, Email: {}", 
                savedUserEntity.getId(), savedUserEntity.getEmail());
            
            User savedUser = userMapper.toDomain(savedUserEntity);
            loggingService.logDebug("AdapterRegister: Usuario convertido de entidad a dominio - ID: {}, Email: {}", 
                savedUser.getId(), savedUser.getEmail());
            
            return savedUser;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al guardar usuario - Email: {}", user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            loggingService.logDebug("AdapterRegister: Buscando usuario por email: {}", email);
            
            Optional<User> user = ormRegister.findByEmail(email)
                    .map(userMapper::toDomain);
            
            if (user.isPresent()) {
                loggingService.logDebug("AdapterRegister: Usuario encontrado - ID: {}, Email: {}", 
                    user.get().getId(), user.get().getEmail());
            } else {
                loggingService.logDebug("AdapterRegister: Usuario no encontrado - Email: {}", email);
            }
            
            return user;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al buscar usuario por email: {}", email, e);
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            loggingService.logDebug("AdapterRegister: Verificando existencia de usuario por email: {}", email);
            
            boolean exists = ormRegister.existsByEmail(email);
            
            loggingService.logDebug("AdapterRegister: Usuario {} existe - Email: {}", 
                exists ? "SÍ" : "NO", email);
            
            return exists;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al verificar existencia de usuario - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            loggingService.logInfo("AdapterRegister: Buscando usuario en la base de datos por ID: {}", id);
            
            Optional<UserEntity> userEntity = ormRegister.findById(id);
            
            if (userEntity.isPresent()) {
                loggingService.logDebug("AdapterRegister: Usuario encontrado en la base de datos - ID: {}", id);
                User user = userMapper.toDomain(userEntity.get());
                return Optional.of(user);
            } else {
                loggingService.logWarning("AdapterRegister: Usuario no encontrado en la base de datos - ID: {}", id);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al buscar usuario por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try {
            loggingService.logInfo("AdapterRegister: Eliminando usuario de la base de datos - ID: {}", id);
            
            Optional<UserEntity> userEntity = ormRegister.findById(id);
            
            if (userEntity.isPresent()) {
                ormRegister.delete(userEntity.get());
                loggingService.logDebug("AdapterRegister: Usuario eliminado de la base de datos - ID: {}", id);
                
                User deletedUser = userMapper.toDomain(userEntity.get());
                return Optional.of(deletedUser);
            } else {
                loggingService.logWarning("AdapterRegister: Usuario no encontrado en la base de datos para eliminar - ID: {}", id);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al eliminar usuario - ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<User> update(Long id, User user) {
         try {
            loggingService.logInfo("AdapterRegister: Actualizando usuario en la base de datos - ID: {}, Email: {}", 
                id, user.getEmail());
            
            Optional<UserEntity> userExisting = ormRegister.findById(id);
            
            if (userExisting.isPresent()) {
                UserEntity userSaved = userExisting.get();
                user.setId(userSaved.getId());
                
                UserEntity updatedUserEntity = ormRegister.save(userMapper.toEntity(user));
                loggingService.logDebug("AdapterRegister: Usuario actualizado en la base de datos - ID: {}, Email: {}", 
                    id, updatedUserEntity.getEmail());
                
                User updatedUser = userMapper.toDomain(updatedUserEntity);
                return Optional.of(updatedUser);
            } else {
                loggingService.logWarning("AdapterRegister: Usuario no encontrado en la base de datos para actualizar - ID: {}", id);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al actualizar usuario - ID: {}, Email: {}", 
                id, user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public List<User> findByFilters(String name, String lastName, String rol) {
        try {
            loggingService.logInfo("AdapterRegister: Buscando usuarios en la base de datos con filtros - Nombre: {}, Apellido: {}, Rol: {}", 
                name, lastName, rol);
            
            List<User> users = ormRegister.findByFilters(name, lastName, rol).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
            
            loggingService.logInfo("AdapterRegister: Se encontraron {} usuarios en la base de datos", users.size());
            
            return users;
            
        } catch (Exception e) {
            loggingService.logError("AdapterRegister: Error al buscar usuarios por filtros - Nombre: {}, Apellido: {}, Rol: {}", 
                name, lastName, rol, e);
            throw e;
        }
    }
    
}