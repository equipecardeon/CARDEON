![Cardeon](https://img.shields.io/badge/Projeto-Cardeon-yellow?style=for-the-badge&logo=github&logoColor=black)
![Inclusão & Solidariedade](https://img.shields.io/badge/INCLUSÃO_%26_SOLIDARIEDADE-criando%20um%20mundo%20de%20novas%20possibilidades-001A33?style=for-the-badge&labelColor=6E6E6E&color=001A33&logoColor=white)

# 📱👀👁️‍🗨️ CARDEON - APLICATIVO MOBILE PARA AUXÍLIO DE DEFICIENTES VISUAIS

---

## 📌 Sobre 🧭

>Cardeon é um aplicativo móvel com visão computacional capaz de realizar detecção de classes (objetos, pessoas, animais, automóveis entre outros).
>Permitindo maior autonomia e inclusão para pessoas com perca de visão parcial ou total.

</div>

 **PROBLEMA⚠️:**
A deficiência visual representa um dos principais desafios relacionados à inclusão
social, autonomia e acessibilidade na contemporaneidade. Segundo dados do Instituto
Brasileiro de Geografia e Estatística (IBGE, 2022), milhões de brasileiros convivem
diariamente com limitações permanentes relacionadas à visão, enfrentando dificuldades no
acesso à informação, na mobilidade e na interação segura com diferentes ambientes.
Esse cenário evidencia a necessidade de soluções tecnológicas capazes de promover
maior independência e inclusão digital. Além de evidenciar barreiras de
acesso a recursos digitais que restringem significativamente a participação social, educacional e
profissional de pessoas com deficiência visual.

<p align="center">
  <img 
       src="https://github.com/user-attachments/assets/3704657f-0f32-4404-8bd0-897a277a926e"
       width="700"
  />
</p>

 **SOLUÇÃO PROPOSTA💡:**
O projeto Cardeon, inspirado no
conceito de pontos cardeais como símbolo de orientação e direcionamento, surge como uma
proposta de tecnologia assistiva baseada em inteligência artificial e visão computacional. A
solução foi desenvolvida com o objetivo de auxiliar pessoas com deficiência visual por meio
da identificação, em tempo real, de pessoas, animais, objetos comuns e objetos
potencialmente perigosos, fornecendo respostas sonoras acessíveis ao usuário.

## 📸 Screenshots
<img width="1774" height="754" alt="Image" src="https://github.com/user-attachments/assets/9adfaf44-1460-4306-a173-06edb98ed259" />

**PRINCIPAIS RECURSOS DO APP 📱🧠:**
- ✔Visão computacional (YOLO) ✔ Detecta objetos em tempo real ✔ Usa modelo yolov8n.pt (leve) ✔ Identifica classes como: pessoa, carro, gato, cachorro, celular e bicicleta 📷
- ✔Sistema de detecção com caixas ✔ Desenha bounding boxes verdes ✔ Mostra nome do objeto na tela ✔.
- ✔Fala automaticamente os objetos detectados. Exemplo: "pessoa, carro, cachorro".
- ✔Não necessita de aparelhos robustos e tecnológicos.
- ✔Experiência fluida e rápida, evitando falhas e lags (atraso entre ação e a sua resposta para o usuário).


⚠️**O MVP atualmente faz a detecção com classes em inglês.**


 **CASOS DE USO PRINCIPAIS💡:**
 - ✔Detecção de objetos;
 - ✔Reconhecimento no ambiente do cotidiano;
 - ✔Melhor adaptação em espaço educional;
 - ✔Ampliação da autonomia na sociedade;
 - ✔Suprir necessidades em relação a poucos recursos assitivos nas cidades.`

**TECNOLOGIA UTILIZADA🌐:**

- Visual Studio Code (VS Code): Editor de código-fonte plataforma resnposável pela programação inicial. ![VS Code](https://img.shields.io/badge/VS%20Code-007ACC?logo=visual-studio-code&logoColor=white)
- Linguagem de programação: Java. ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
- Android Studio Code: É a IDE oficial do Google para desenvolver aplicativos Android. ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
- YOLOv8 + Ultralytics: Modelo de detecção de objetos em tempo real, tecnicamente é uma CNN (Rede Neural Convolucional). ![Ultralytics](https://img.shields.io/badge/Ultralytics-YOLOv8-5A00FF?logo=ultralytics&logoColor=white)
- TensorFlow Lite Converter: Responsável pela conversão do modelo YOLOv8 para TensorFlow Lite (TFLite), permitindo execução em dispositivos Android sem dependência da biblioteca Ultralytics. ![TensorFlow](https://img.shields.io/badge/TensorFlow-FF6F00?logo=tensorflow&logoColor=white)
- Threading (multithreading):Biblioteca em Python responsável por executar tarefas paralelas evitando o travamento do loop. ![Threading](https://img.shields.io/badge/Threading-Multithreading-blue.svg)

**Manual de execução do MVP:**
  1. Instale o Android Studio Code e o Java JDK 17.
  2. Abra o Android Studio Code.
  3. Vá em 'File', depois em 'Open'.
  4. Navegue até a pasta do projeto e a escolha.
  5. Depois de clicar em 'ok', sincronizar a gradles; Clique no símbolo do "elefante" para sincronizá-las.
  6. Logo após, clique na opção 'run' para executar todos os arquivos do projeto.
  7. No terminal, verificar algum erro de execução.
  8. Caso não ocorra erro, o código será conectado no celular, via escaneamento com QR Code.
  9. Após todos esses passos, o programa funcionará corretamente.
 
**PRÉ-REQUISITOS PARA RODAR NO MOBILE:**
- 📝Android 8 ou superior✅
- 📝Pelo menos: 2 GB livres✅
- 📝O usuário precisa permitir: câmera 📷 e microfone 🎤 (se usar voz)✅
- 📝Câmera funcionando porque o app vai usar: preview da câmera e detecção em tempo real✅

**Manual do usuário:**
O aplicativo consiste na acessibilidade assistiva do público-alvo, visando facilidade ao entrar no app.
- 1° O usuário irá procurar o aplicativo no seu celular utilizando os recursos de acessibilidade para pessoas com deficiência visual.
- 2° O aplicativo abrirá instantaneamente.
- 3° O software começará a analisar o ambiente em sua volta.
- 4° Retornará uma resposta sonora quando identificar algum objeto, ou seja, o aplicativo irá comunicar qual objeto detectou.
- 5° O usuário conseguirá saber qual objeto foi detectado.

Observação: O aplicativo ainda está em desenvolvimento, entretanto, seu recurso principal de detecção de objetos e fala já está incluso.


**ATUALIZAÇÕES FUTURAS:**
- ⏱️Recursos premium de personalização da experiência sonora.
- ⏱️Interface tátil-sonora.
- ⏱️Aprimoramento da precisão do reconhecimento de classes.
- ⏱️Ampliação dos recursos de acessibilidade.
- ⏱️Adição de novas classes.
- ⏱️Tradução das classes para português.

**Responsáveis pela transformação do código fonte em mobile:👤**

- Isac Leal
- Victor Eduardo

**Colaborador:**
- Utilização da tecnologia de IA GEMINI para os estudos de transformação do aplicativo anteriomente desktop para mobile.


## 🧩 Guidelines

### 📌 Commits Pattern

- **feat:** Nova funcionalidade  
- **fix:** Correção de bugs  
- **style:** Alterações de estilo/formatação  
- **refactor:** Melhorias de código sem mudança funcional  
- **docs:** Documentação  
- **perf:** Otimizações de performance  
- **test:** Testes  
- **chore:** Configurações e dependências

**EQUIPE CARDEON⭐🫂:**
> A inclusão começa em novas possibilidades de enxegar o mundo.
<img width="1280" height="960" alt="Image" src="https://github.com/user-attachments/assets/06e7e6b1-99ea-459f-b728-497540e0eda1" />
