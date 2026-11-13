<template>
 <AdminLayout>
  <div class="d-flex flex-wrap justify-space-between align-center mb-5 ga-3">
   <div><h1 class="text-h4 font-weight-bold">Dispositivos</h1><p class="text-medium-emphasis mt-1">Gerencie os sensores, seus responsáveis e estados de operação.</p></div>
   <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNovo">Novo dispositivo</v-btn>
  </div>
  <v-alert v-if="mensagem" class="mb-4" type="success" closable @click:close="mensagem=''">{{ mensagem }}</v-alert>
  <v-alert v-if="erro" class="mb-4" type="error" closable @click:close="erro=''">{{ erro }}</v-alert>
  <v-card rounded="lg" elevation="1"><v-data-table :headers="headers" :items="dispositivos" :loading="carregando" no-data-text="Nenhum dispositivo cadastrado.">
   <template #item.usuario="{item}">{{ item.usuario?.nome || 'Não vinculado' }}</template>
   <template #item.status="{item}"><v-chip size="small" :color="item.status==='ATIVO'?'success':item.status==='INATIVO'?'secondary':'warning'" variant="tonal">{{ nomesStatus[item.status]||item.status||'Não definido' }}</v-chip></template>
   <template #item.actions="{item}">
    <v-btn icon="mdi-pencil" size="small" variant="text" color="primary" aria-label="Editar dispositivo" @click="abrirEdicao(item)" />
    <v-btn icon="mdi-delete" size="small" variant="text" color="error" aria-label="Excluir dispositivo" @click="excluir(item)" />
   </template>
  </v-data-table></v-card>
  <v-dialog v-model="dialog" max-width="560"><v-card rounded="lg">
    <v-card-title class="pa-5">{{ editandoId ? 'Editar dispositivo' : 'Cadastrar dispositivo' }}</v-card-title>
    <v-card-text><v-form ref="formulario" @submit.prevent="salvar">
      <v-text-field v-model="form.nome" label="Nome do dispositivo" maxlength="100" :rules="[v=>!!v?.trim()||'Informe um nome']" required />
      <v-text-field v-model="form.localizacao" label="Localização" maxlength="100" :rules="[v=>!!v?.trim()||'Informe a localização']" required />
      <v-select v-model="form.status" :items="opcoesStatus" label="Status" :rules="[v=>!!v||'Selecione um status']" required />
      <v-select v-model="form.usuarioId" :items="opcoesUsuarios" label="Usuário responsável (opcional)" clearable hint="Cadastre usuários na aba Usuários para vincular sensores." persistent-hint />
    </v-form></v-card-text>
    <v-card-actions class="pa-4"><v-spacer/><v-btn variant="text" @click="dialog=false">Cancelar</v-btn><v-btn color="primary" :loading="salvando" @click="salvar">Salvar</v-btn></v-card-actions>
  </v-card></v-dialog>
 </AdminLayout>
</template>
<script setup>
import {ref,onMounted,computed} from 'vue'
import {listarDispositivos,criarDispositivo,editarDispositivo,excluirDispositivo,listarUsuarios,nomesStatus} from '@/services/noisehunter_services'
const headers=[{title:'ID',key:'id'},{title:'Nome',key:'nome'},{title:'Localização',key:'localizacao'},{title:'Status',key:'status'},{title:'Responsável',key:'usuario'},{title:'Ações',key:'actions',sortable:false}]
const dispositivos=ref([]),usuarios=ref([]),dialog=ref(false),editandoId=ref(null),formulario=ref(null),salvando=ref(false),carregando=ref(false),mensagem=ref(''),erro=ref('')
const opcoesStatus=Object.entries(nomesStatus).map(([value,title])=>({value,title}))
const opcoesUsuarios=computed(()=>usuarios.value.map(u=>({title:`${u.nome} (${u.email})`,value:u.id})))
const vazio=()=>({nome:'',localizacao:'',status:'ATIVO',usuarioId:null})
const form=ref(vazio())
async function carregar(){carregando.value=true;try{const [ds,us]=await Promise.all([listarDispositivos(),listarUsuarios()]);dispositivos.value=ds;usuarios.value=us}catch(e){erro.value='Erro ao carregar dispositivos. Verifique o servidor.'}finally{carregando.value=false}}
function abrirNovo(){editandoId.value=null;form.value=vazio();dialog.value=true;erro.value=''}
function abrirEdicao(d){editandoId.value=d.id;form.value={nome:d.nome,localizacao:d.localizacao,status:d.status||'ATIVO',usuarioId:d.usuario?.id||null};dialog.value=true;erro.value=''}
async function salvar(){const validacao=await formulario.value?.validate();if(!validacao?.valid)return;salvando.value=true;erro.value='';try{
  if(editandoId.value){await editarDispositivo(editandoId.value,form.value);mensagem.value='Dispositivo atualizado.'}
  else{await criarDispositivo(form.value);mensagem.value='Dispositivo cadastrado.'}
  dialog.value=false;await carregar()
 }catch(e){erro.value=e.response?.data?.mensagem||'Não foi possível salvar o dispositivo.'}finally{salvando.value=false}}
async function excluir(item){if(!confirm(`Excluir dispositivo "${item.nome}"?`))return;erro.value='';try{await excluirDispositivo(item.id);mensagem.value='Dispositivo removido.';await carregar()}catch(e){erro.value=e.response?.data?.mensagem||'Não foi possível excluir.'}}
onMounted(carregar)
</script>
