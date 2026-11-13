<template>
 <AdminLayout>
  <div class="d-flex flex-wrap justify-space-between align-center mb-5 ga-3">
   <div><h1 class="text-h4 font-weight-bold">Usuários</h1><p class="text-medium-emphasis mt-1">Cadastre os responsáveis pelos dispositivos do NoiseHunter.</p></div>
   <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNovo">Novo usuário</v-btn>
  </div>
  
  <v-alert v-if="mensagem" type="success" class="mb-4" closable @click:close="mensagem=''">{{ mensagem }}</v-alert>
  <v-alert v-if="erro" type="error" class="mb-4" closable @click:close="erro=''">{{ erro }}</v-alert>
  <v-card rounded="lg" elevation="1">
   <v-data-table :headers="headers" :items="usuarios" :loading="carregando" no-data-text="Nenhum usuário cadastrado.">
    <template #item.actions="{item}">
     <v-btn icon="mdi-pencil" size="small" variant="text" color="primary" aria-label="Editar usuário" @click="abrirEdicao(item)" />
     <v-btn icon="mdi-delete" size="small" variant="text" color="error" aria-label="Excluir usuário" @click="excluir(item)" />
    </template>
   </v-data-table>
  </v-card>
  <v-dialog v-model="dialog" max-width="530">
   <v-card rounded="lg">
    <v-card-title class="pa-5">{{ editandoId ? 'Editar usuário' : 'Cadastrar usuário' }}</v-card-title>
    <v-card-text><v-form ref="formulario" @submit.prevent="salvar">
     <v-text-field v-model="form.nome" label="Nome" maxlength="100" :rules="[v=>!!v?.trim()||'Informe o nome']" required />
     <v-text-field v-model="form.email" label="E-mail" type="email" maxlength="100" :rules="[v=>/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)||'Informe um e-mail válido']" required />
     <v-text-field v-model="form.senha" label="Senha" type="password" autocomplete="new-password" maxlength="72" :hint="editandoId ? 'Deixe vazia para manter a senha atual.' : 'Mínimo 8 caracteres.'" persistent-hint :rules="[v=>(editandoId && !v) || (v?.length>=8) || 'Mínimo de 8 caracteres']" />
    </v-form></v-card-text>
    <v-card-actions class="pa-4"><v-spacer /><v-btn variant="text" @click="dialog=false">Cancelar</v-btn><v-btn color="primary" :loading="salvando" @click="salvar">Salvar</v-btn></v-card-actions>
   </v-card>
  </v-dialog>
 </AdminLayout>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listarUsuarios, criarUsuario, editarUsuario, excluirUsuario } from '@/services/noisehunter_services'
const headers=[{title:'ID',key:'id'},{title:'Nome',key:'nome'},{title:'E-mail',key:'email'},{title:'Ações',key:'actions',sortable:false}]
const usuarios=ref([]), dialog=ref(false), editandoId=ref(null), formulario=ref(null)
const salvando=ref(false), carregando=ref(false), mensagem=ref(''), erro=ref('')
const vazio=()=>({nome:'',email:'',senha:''})
const form=ref(vazio())
async function carregar(){carregando.value=true;try{usuarios.value=await listarUsuarios()}catch(e){erro.value='Erro ao consultar os usuários.'}finally{carregando.value=false}}
function abrirNovo(){editandoId.value=null;form.value=vazio();erro.value='';dialog.value=true}
function abrirEdicao(u){editandoId.value=u.id;form.value={nome:u.nome,email:u.email,senha:''};erro.value='';dialog.value=true}
async function salvar(){const validacao=await formulario.value?.validate();if(!validacao?.valid)return;salvando.value=true;erro.value='';try{
 if(editandoId.value){await editarUsuario(editandoId.value,form.value);mensagem.value='Usuário atualizado.'}
 else{await criarUsuario(form.value);mensagem.value='Usuário cadastrado.'}
 dialog.value=false;await carregar()
 }catch(e){erro.value=e.response?.data?.mensagem||'Não foi possível salvar o usuário.'}finally{salvando.value=false}}
async function excluir(item){if(!confirm(`Excluir usuário "${item.nome}"?`))return;erro.value='';try{await excluirUsuario(item.id);mensagem.value='Usuário removido.';await carregar()}catch(e){erro.value=e.response?.data?.mensagem||'Não foi possível excluir o usuário.'}}
onMounted(carregar)
</script>
