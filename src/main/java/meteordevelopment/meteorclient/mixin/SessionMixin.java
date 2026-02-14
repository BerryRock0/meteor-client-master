package meteordevelopment.meteorclient.mixin;

import java.util.UUID;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.session.Session;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.ServerSpoof;

@Mixin(Session.class)
public abstract class SessionMixin
{
  @Inject(method = "getAccessToken()Ljava/lang/String;", at=@At("HEAD"))
  public void spoofAccessToken(String string, CallbackInfoReturnable<String> cir) 
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofAccessToken.get())
      cir.setReturnValue(serverSpoof.accessTokenString.get());
  }

  @Inject(method = "getClientId()Ljava/util/Optional;", at=@At("HEAD"))
  public void spoofClientId(String string, CallbackInfoReturnable<String> cir)
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofClientId.get())
      cir.setReturnValue(serverSpoof.clientIdString.get());
  }
  
  @Inject(method = "getSessionId()Ljava/lang/String;", at=@At("HEAD"))
  public void spoofSessionId(String string, CallbackInfoReturnable<String> cir)
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofSessionId.get())
      cir.setReturnValue(serverSpoof.sessionIdString.get());
  }
  
  @Inject(method = "getUsername()Ljava/lang/String;", at=@At("HEAD"))
  public void spoofUsername(String string, CallbackInfoReturnable<String> cir)
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofUsername.get())
      cir.setReturnValue(serverSpoof.usernameString.get());
  }
  
  @Inject(method = "getUuidOrNull()Ljava/util/UUID;", at=@At("HEAD"))
  public void spoofUuidOrNull(UUID uuid, CallbackInfoReturnable<UUID> cir)
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofUuid.get())
      cir.setReturnValue(UUID.fromString(serverSpoof.uuidString.get()));
  }
  
  @Inject(method = "getXuid()Ljava/util/Optional;", at=@At("HEAD"))
  public void spoofXuid(String string, CallbackInfoReturnable<String> cir)
  {
    ServerSpoof serverSpoof = Modules.get().get(ServerSpoof.class);
    if(serverSpoof.isActive() && serverSpoof.spoofXuid.get())
      cir.setReturnValue(serverSpoof.xuidString.get());
  }  
}
