# Capacita Rota

Aplicativo Android de leitura de medidores em campo. Projeto final do Módulo Avançado da turma Android do Capacita iRede.

O app simula a rotina de um leiturista. Ele recebe uma rota de pontos de atendimento, visita cada endereço, registra a leitura do medidor com foto e localização, guarda tudo no aparelho e envia para o servidor depois.

A visita é sempre gravada primeiro no banco local e só então sincronizada com a API. Nenhuma leitura se perde quando a rede falha.

## Telas

São três abas e duas telas de detalhe, todas navegáveis por Navigation Compose.

| Tela | Rota | Função |
|---|---|---|
| Rota | `route/list` | Lista os pontos de atendimento e quantos já foram visitados. |
| Mapa | `map/canvas` | Mostra os pontos posicionados geograficamente. |
| Envio | `sync/list` | Agrupa as visitas por situação e dispara a sincronização. |
| Detalhe do ponto | `route/point/{pointId}` e `map/point/{pointId}` | Dados do ponto, última visita e campo da nova leitura. |
| Visita | `visit/{pointId}/{reading}` | Tela cheia para anexar foto, capturar GPS e salvar. |

O fluxo é: Rota, detalhe do ponto, visita, salvar, envio.

## Funcionalidades

- Rota com 7 pontos reais da Aldeota (Fortaleza-CE), carregados no primeiro uso e guardados no banco local.
- Mapa desenhado em Compose com `Canvas`, projetando as coordenadas na tela sem depender de SDK de mapas.
- Validação da leitura: recusa texto não numérico e valores menores que a leitura anterior do medidor.
- Foto de evidência pela câmera, guardada via `FileProvider`.
- Captura de GPS no momento da visita.
- Sincronização com estado por visita: aguardando envio, enviando, enviada, falha.
- Notificações locais ao salvar e durante o envio, com ação "Enviar agora" na bandeja.
- Tema claro e escuro, com rótulos semânticos e contraste verificado por teste.

## Requisitos do projeto

### 1. Interface gráfica em Jetpack Compose

Cinco telas. A navegação usa um `NavHost` com três grafos aninhados, um por aba, em [CapacitaApp.kt](app/src/main/java/com/example/capacita_projeto_final/ui/CapacitaApp.kt), com argumentos tipados (`NavType.IntType`) e transição própria na tela de visita. As chamadas de navegação ficam centralizadas em [AppCoordinator.kt](app/src/main/java/com/example/capacita_projeto_final/navigation/AppCoordinator.kt).

Os componentes reutilizáveis estão em [ui/components/](app/src/main/java/com/example/capacita_projeto_final/ui/components/): barra de abas, barra de navegação, listas, botões, alertas, action sheet, estado vazio e feedback tátil. Tema, cores, tipografia, formas e métricas ficam em [ui/theme/](app/src/main/java/com/example/capacita_projeto_final/ui/theme/).

### 2. Persistência local com Room

Duas entidades: [`RoutePointEntity`](app/src/main/java/com/example/capacita_projeto_final/features/route/data/local/RoutePointEntity.kt) e [`VisitEntity`](app/src/main/java/com/example/capacita_projeto_final/features/visit/data/local/VisitEntity.kt), ligadas por chave estrangeira com `CASCADE`.

Dois DAOs: [`RoutePointDao`](app/src/main/java/com/example/capacita_projeto_final/features/route/data/local/RoutePointDao.kt) e [`VisitDao`](app/src/main/java/com/example/capacita_projeto_final/features/visit/data/local/VisitDao.kt).

A leitura é reativa com `Flow` (`observeAll`, `observeById`, `observeLatestForPoint`) e a gravação usa `@Insert` e `UPDATE`. O banco tem uma migração de versão em [DatabaseMigrations.kt](app/src/main/java/com/example/capacita_projeto_final/core/database/DatabaseMigrations.kt).

### 3. Consumo de API com Retrofit

Duas requisições em [CapacitaApi.kt](app/src/main/java/com/example/capacita_projeto_final/features/sync/data/remote/CapacitaApi.kt), usadas pelo [SyncRepository.kt](app/src/main/java/com/example/capacita_projeto_final/features/sync/data/SyncRepository.kt):

- `GET` em `getServiceStatus()`, que consulta o serviço antes de sincronizar.
- `POST` em `sendVisit()`, que envia cada visita pendente.

A API é a JSONPlaceholder (`https://jsonplaceholder.typicode.com/`), pública e sem necessidade de cadastro. Ela responde ao POST, mas não persiste o dado de fato.

### Requisitos opcionais

Recursos nativos do aparelho:

- Câmera, para a foto de evidência, gravada em arquivo próprio do app ([PhotoEvidenceStore.kt](app/src/main/java/com/example/capacita_projeto_final/features/visit/infrastructure/PhotoEvidenceStore.kt)).
- GPS, pelo Fused Location Provider ([DeviceLocationProvider.kt](app/src/main/java/com/example/capacita_projeto_final/features/visit/infrastructure/DeviceLocationProvider.kt)).
- Feedback tátil ao salvar a visita e ao errar a leitura ([HigHaptics.kt](app/src/main/java/com/example/capacita_projeto_final/ui/components/HigHaptics.kt)).

Notificações locais, em [core/notification/](app/src/main/java/com/example/capacita_projeto_final/core/notification/):

- Aviso de visita salva, com a instalação e a leitura.
- Barra de progresso durante o envio.
- Botão "Enviar agora" na notificação, tratado por um `BroadcastReceiver` ([SyncActionReceiver.kt](app/src/main/java/com/example/capacita_projeto_final/core/notification/SyncActionReceiver.kt)), que sincroniza sem abrir o app.

A permissão `POST_NOTIFICATIONS` do Android 13+ é pedida em execução. Se for negada, o app continua funcionando e o registro da visita não é afetado.

## Arquitetura

O código é organizado por funcionalidade, com MVVM na camada de apresentação.

```
features/<funcionalidade>/
├── data/            repositórios, Room (local/) e Retrofit (remote/)
├── domain/          modelos e regras de negócio, sem dependência de Android
├── infrastructure/  acesso a câmera e GPS
└── presentation/    telas Compose e ViewModels
```

A injeção de dependência é manual, via [`AppContainer`](app/src/main/java/com/example/capacita_projeto_final/core/AppContainer.kt), criado uma vez em [`CapacitaApplication`](app/src/main/java/com/example/capacita_projeto_final/CapacitaApplication.kt). O ViewModel expõe um `StateFlow` de estado e a tela recebe esse estado mais os callbacks, sem guardar lógica.

Validação de leitura, projeção do mapa e conteúdo das notificações são funções puras, o que permite testá-las sem emulador.

## Tecnologias

| Recurso | Versão |
|---|---|
| Kotlin | Compose BOM 2026.08.00, Material 3 |
| Navegação | Navigation Compose 2.8.8 |
| Banco local | Room 2.8.4 com KSP |
| Rede | Retrofit 3.0.0 e conversor Gson |
| Localização | Play Services Location 21.3.0 |
| Assincronismo | Coroutines e Flow |
| Build | Gradle 9.7.1, AGP 9.3.2 |
| SDK | minSdk 24, targetSdk 37, compileSdk 37 |
| Java | 17 |

## Como rodar

Você precisa do Android Studio (ou do SDK por linha de comando), JDK 17 e um emulador ou aparelho com Android 7.0 (API 24) ou superior.

### Android Studio

1. Clone o repositório e abra a pasta no Android Studio.
2. Espere o Gradle Sync terminar.
3. Escolha o emulador ou conecte o aparelho com a depuração USB ativada.
4. Clique em Run.

O `local.properties`, que aponta para o SDK, é gerado pelo próprio Android Studio ao abrir o projeto.

### Linha de comando

```bash
./gradlew assembleDebug      # Linux e macOS
.\gradlew.bat assembleDebug  # Windows
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`. Para compilar e instalar direto no aparelho conectado, use `./gradlew installDebug`.

Se o SDK não for encontrado, crie um `local.properties` na raiz com o caminho dele:

```properties
sdk.dir=/caminho/para/Android/Sdk
```

No Windows, escape as barras: `C\:\\Users\\usuario\\AppData\\Local\\Android\\Sdk`.

### Permissões

O app pede câmera, localização e notificações conforme você usa cada recurso. Todas são opcionais. Negar qualquer uma não impede registrar nem enviar visitas, o app apenas deixa de anexar aquele dado.

## Instalando o APK pronto

O arquivo `capacita-rota.apk` está na raiz do projeto. É um build de debug, com cerca de 20 MB.

Para instalar pelo aparelho, copie o arquivo para o celular, toque nele e autorize a instalação de fontes desconhecidas. Pelo computador:

```bash
adb install capacita-rota.apk
```

O app foi instalado e executado em um Samsung Galaxy A06 (SM-A065M) com Android 16, arm64-v8a, com as três abas navegando sem erro. O `targetSdk 37` não impede rodar em versões anteriores a partir da API 24.

### Erros comuns na instalação

`INSTALL_FAILED_UPDATE_INCOMPATIBLE: signatures do not match` acontece quando já existe uma versão do app no aparelho assinada com outra chave de debug, por exemplo instalada a partir de outro computador. Desinstale antes:

```bash
adb uninstall com.example.capacita_projeto_final
adb install capacita-rota.apk
```

A desinstalação apaga os dados locais, ou seja, as visitas já registradas naquele aparelho.

Se o celular não aparecer no `adb devices`, ative as Opções do desenvolvedor tocando sete vezes em Número da versão, em Configurações e Sobre o telefone. Depois ligue a Depuração USB e autorize a impressão digital RSA que aparece ao conectar o cabo.

## Testes

São 23 testes unitários, todos passando.

| Teste | Cobertura |
|---|---|
| `ReadingValidatorTest` | Validação da leitura do medidor |
| `RouteMapProjectionTest` | Projeção das coordenadas no mapa |
| `SyncPayloadTest` | Corpo enviado à API |
| `SyncFeedbackTest` | Mensagens de resultado da sincronização |
| `VisitNotificationTest` | Conteúdo e comportamento das notificações |
| `HigColorContrastTest` | Contraste das cores do tema |

```bash
./gradlew testDebugUnitTest
```

Os testes instrumentados (`VisitScreenDiscardTest` e `HigNavigationBarTest`) precisam de emulador ou aparelho conectado:

```bash
./gradlew connectedDebugAndroidTest
```

## Estrutura de pastas

```
app/src/main/java/com/example/capacita_projeto_final/
├── core/
│   ├── database/          CapacitaDatabase e migrações
│   ├── notification/      canal, conteúdo, publicação e ação das notificações
│   ├── AppContainer.kt    injeção de dependência manual
│   └── ViewModelFactory.kt
├── features/
│   ├── route/             rota e mapa
│   ├── point/             detalhe do ponto de atendimento
│   ├── visit/             registro da visita, câmera, GPS e Room
│   └── sync/              Retrofit e tela de envio
├── navigation/            rotas, abas e coordenador de navegação
├── ui/
│   ├── components/        componentes Compose reutilizáveis
│   ├── theme/             cores, tipografia, formas, métricas
│   └── CapacitaApp.kt     NavHost e composição das abas
├── CapacitaApplication.kt
└── MainActivity.kt
```
