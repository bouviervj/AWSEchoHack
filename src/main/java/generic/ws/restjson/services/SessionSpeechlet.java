package generic.ws.restjson.services;

import generic.connect.CallTempServer;
import generic.ws.server.EmbeddedServer;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class SessionSpeechlet implements Speechlet {
    protected static final Logger log = Logger.getLogger(EmbeddedServer.class.getName());
    private static final String ROOM_KEY = "Room";
    //private static final String TEMPERATURE_SLOT = "Temperature";

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}"+ request.getRequestId()+ " " +
                session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}" + request.getRequestId() + " "+
                session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}"+ request.getRequestId()+ " "+
                session.getSessionId());

        // Get intent from the request object.
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        // Note: If the session is started with an intent, no welcome message will be rendered;
        // rather, the intent specific response will be returned.
        if ("WhatsTheTemperatureIntent".equals(intentName)) {
            return getTempFromSession(intent, session);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}" + request.getRequestId() +" " +
                session.getSessionId());
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual welcome message
     */
    private SpeechletResponse getWelcomeResponse() {
        // Create the welcome message.
        String speechOutput =
                "Welcome to the Alexa Temperature Webservice, "
                        + "You can ask me the temperature in any room of your house by saying, for example, what is the temperature in the kitchen, or, what is the temperature in the baby room?";

        String repromptText =
                "You can ask me the temperature in any room of your house by saying, for example, what is the temperature in the kitchen, or, what is the temperature in the baby room?";

        // Here we are setting shouldEndSession to false to not end the session and
        // prompt the user for input
        return buildSpeechletResponse("Welcome", speechOutput, repromptText, false);
    }

  

    /**
     * Creates a {@code SpeechletResponse} for the intent and get the user's favorite color from the
     * Session.
     *
     * @param intent
     *            intent for the request
     * @return SpeechletResponse spoken and visual response for the intent
     */
    private SpeechletResponse getTempFromSession(final Intent intent, final Session session) {
        String speechOutput = "";
        boolean shouldEndSession = false;
        
        Map<String, Slot> slots = intent.getSlots();

        // Get the color slot from the list of slots.
        Slot tempSlot = slots.get(ROOM_KEY);

        // Get the user's favorite color from the session.
        String room = tempSlot.getValue();

        int temperature = CallTempServer.askTemperature(room);
        
        // Check to make sure user's favorite color is set in the session.
        if (temperature!=-1) {
            speechOutput = String.format("The temperature is %d degrees ", temperature);
            //shouldEndSession = true;
        } else {
            // Since the user's favorite color is not set render an error message.
            speechOutput =
                    "I'm not able to find the temperature in this room.";
        }
        	
        // In this case we do not want to reprompt the user so we return a null Reprompt object
        return buildSpeechletResponse(intent.getName(), speechOutput, null, shouldEndSession);
    }

    /**
     * Creates and returns the visual and spoken response with shouldEndSession flag
     *
     * @param title
     *            title for the companion application home card
     * @param output
     *            output content for speech and companion application home card
     * @param repromptText
     *            the text the should be spoken to the user if they user either fails to reply to a
     *            question or says something that was not understood
     * @param shouldEndSession
     *            should the session be closed
     * @return SpeechletResponse spoken and visual response for the given input
     */
    private SpeechletResponse buildSpeechletResponse(final String title, final String output,
            final String repromptText, final boolean shouldEndSession) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(String.format("SessionSpeechlet - %s", title));
        card.setContent(String.format("SessionSpeechlet - %s", output));

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);

        // Create the speechlet response.
        SpeechletResponse response = new SpeechletResponse();
        response.setShouldEndSession(shouldEndSession);
        response.setOutputSpeech(speech);
        response.setCard(card);
        response.setReprompt(buildReprompt(repromptText));
        return response;
    }

    /**
     * Builds the Reprompt object to be used as part of the SpeechletResponse.
     *
     * @param repromptText
     *            The text that will be spoken to the user when a reprompt occurs
     * @return Reprompt
     */
    private Reprompt buildReprompt(final String repromptText) {
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return reprompt;
    }

}