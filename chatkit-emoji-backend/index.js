const express = require("express");
const bodyParser = require("body-parser");
const Pusher = require("pusher");
const Chatkit = require("@pusher/chatkit-server");
const app = express();

var pusher = new Pusher({
  appId: "737257",
  key: "2663259f85e02b7dde58",
  secret: "ad8b22b85cd69e75f3ce",
  cluster: "eu",
  encrypted: true
});

const chatkit = new Chatkit.default({
  instanceLocator: "v1:us1:ffa1e6ad-5dba-40a8-9d2a-a2868a8879bb",
  key:
    "59e3d93b-0421-4d03-b3ef-c6178ad92168:GY1rDDFRCfYTmAXlFI+ZFuju3uGgmKNujQHt10vC/S4="
});
app.use(bodyParser.json());
// app.use(bodyParser.urlencoded({ extended: true }));

// TODO: ADD ROUTES HERE!

app.post("/users", (req, res) => {
  const userId = req.body.userId;
  chatkit
    .createUser({
      id: userId,
      name: userId
    })
    .then(() => {
      res.sendStatus(200);
    })
    .catch(err => {
      console.log(err);
      if (err.error === "services/chatkit/user_already_exists") {
        console.log(`User already exists: ${userId}`);
        res.sendStatus(200);
      } else {
        res.status(err.status).json(err);
      }
    });
});

app.post("/token", (req, res) => {
  const result = chatkit.authenticate({
    userId: req.query.user_id
  });
  res.status(result.status).send(result.body);
});

app.post("/updateEmoji", (req, res) => {
  pusher.trigger(req.body.roomId, "emoji-event", {
    emoji: req.body.emoji,
    count: req.body.count,
    userIds: req.body.userIds,
    messageId: req.body.messageId
  });
  res.send(200);
});

const server = app.listen(3000, () => {
  console.log(`Express server running on port ${server.address().port}`);
});
